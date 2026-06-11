package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import request.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final GameService gameService;


    public WebSocketHandler(GameService gameService) throws DataAccessException {
        this.gameService = gameService;
    }


    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getAuthToken(), command.getUsername(), ctx.session);
                case LEAVE -> leave(command.getGameID(), command.getUsername(), ctx.session);
                case MAKE_MOVE -> move(command.getGameID(), command.getAuthToken(), command.getMove());
                case RESIGN -> resign(command.getGameID(), command.getAuthToken(), command.getUsername(), ctx.session);
            }
        } catch (DataAccessException e) {
            try {
                String msg = e.getMessage();
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg);
                connectionManager.notifyClient(command.getGameID(), ctx.session, serverMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String authToken, String username, Session session) throws IOException, DataAccessException {
        ListRequest r = new ListRequest(authToken);
        GameData[] games = gameService.list(r).games();
        if (gameID < 1 || gameID > games.length) {
            throw new DataAccessException("Error: invalid game ID");
        }
        ChessGame game = games[gameID-1].game();
        connectionManager.add(gameID, session);
        ServerMessage clientMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
        connectionManager.notifyClient(gameID, session, clientMessage);
        String msg = username + " has joined the game";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }

    private void leave(int gameID, String username, Session session) throws IOException {
        connectionManager.remove(gameID, session);
        String msg = username + " has left the game";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }

    private void move(int gameID, String authToken, ChessMove move) throws DataAccessException, IOException {
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, move, false);
        gameService.update(updateRequest);
        String msg = moveToString(move);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, msg, move);
        connectionManager.broadcast(gameID, null, serverMessage);
    }

    private String moveToString(ChessMove move) {
        String[] rank = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] file = {"a", "b", "c", "d", "e", "f", "g", "h"};

        String startRank = rank[move.getStartPosition().getRow()-1];
        String startFile = file[move.getStartPosition().getColumn()-1];
        String start = startFile + startRank;

        String endRank = rank[move.getEndPosition().getRow()-1];
        String endFile = file[move.getEndPosition().getColumn()-1];
        String end = endFile + endRank;

        return start + " " + end;
    }

    private void resign(int gameID, String authToken, String username, Session session) throws DataAccessException, IOException {
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, null, true);
        gameService.update(updateRequest);
        String msg = username + " has resigned";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }
}
