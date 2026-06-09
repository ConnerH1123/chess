package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import request.UpdateRequest;
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
        System.out.println("DEBUG: Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getUsername(), ctx.session);
                case LEAVE -> leave(command.getGameID(), command.getUsername(), ctx.session);
                case MAKE_MOVE -> move(command.getGameID(), command.getAuthToken(), command.getMove(), ctx.session);
                case RESIGN -> resign(command.getGameID(), command.getAuthToken(), command.getUsername(), ctx.session);
            }
        } catch (DataAccessException e) {
            try {
                String msg = e.getMessage();
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg);
                connectionManager.broadcast(command.getGameID(), ctx.session, serverMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("DEBUG: Websocket closed");
    }

    private void connect(int gameID, String username, Session session) throws IOException {
        connectionManager.add(gameID, session);
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

    private void move(int gameID, String authToken, ChessMove move, Session session) throws DataAccessException {
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, move, false);
        gameService.update(updateRequest);
    }

    private void resign(int gameID, String authToken, String username, Session session) throws DataAccessException, IOException {
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, null, true);
        gameService.update(updateRequest);
        String msg = username + " has resigned";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }
}
