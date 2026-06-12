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
import java.util.Objects;

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
                case LEAVE -> leave(command.getGameID(), command.getAuthToken(), command.getUsername(), ctx.session);
                case MAKE_MOVE -> move(command.getGameID(), command.getAuthToken(), command.getUsername(), command.getMove(), ctx.session);
                case RESIGN -> resign(command.getGameID(), command.getAuthToken(), command.getUsername());
            }
        } catch (DataAccessException e) {
            try {
                String msg = e.getMessage();
                ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg);
                connectionManager.notifyClient(ctx.session, serverMessage);
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
        ChessGame game = getChessGame(gameID, authToken);
        connectionManager.add(gameID, session);
        ServerMessage clientMessage = new ServerMessage(game);
        connectionManager.notifyClient(session, clientMessage);
        String msg = username + " has joined the game";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }

    private ChessGame getChessGame(int gameID, String authToken) throws DataAccessException {
        GameData gameData = getGameData(gameID, authToken);
        return gameData.game();
    }

    private GameData getGameData(int gameID, String authToken) throws DataAccessException {
        ListRequest r = new ListRequest(authToken);
        GameData[] games = gameService.list(r).games();
        if (gameID < 1 || gameID > games.length) {
            throw new DataAccessException("Error: invalid game ID");
        }
        return games[gameID-1];
    }

    private void leave(int gameID, String authToken, String username, Session session) throws IOException, DataAccessException {
        if (username == null) {
            username = gameService.getUsername(authToken);
        }
        GameData gameData = getGameData(gameID, authToken);
        String playerColor = getUserColor(gameData, username);
        LeaveRequest r = new LeaveRequest(authToken, gameID, playerColor);
        gameService.leave(r);
        connectionManager.remove(gameID, session);
        String msg = username + " has left the game";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, session, serverMessage);
    }

    private String getUserColor(GameData gameData, String username) {
        if (Objects.equals(gameData.whiteUsername(), username)) {
            return "WHITE";
        }
        else if (Objects.equals(gameData.blackUsername(), username)) {
            return "BLACK";
        }
        else {
            return null;
        }
    }

    private void move(int gameID, String authToken, String username, ChessMove move, Session session) throws DataAccessException, IOException {
        if (username == null) {
            username = gameService.getUsername(authToken);
        }
        validateUsername(gameID, authToken, username);
        validateGameState(getChessGame(gameID, authToken));
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, move, false);
        gameService.update(updateRequest);
        ChessGame game = getChessGame(gameID, authToken);
        ServerMessage loadGameMessage = new ServerMessage(game, move);
        connectionManager.broadcast(gameID, null, loadGameMessage);
        String moveMade = moveToString(move);
        ServerMessage moveMadeMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMade);
        connectionManager.broadcast(gameID, session, moveMadeMessage);
        switch (game.getGameStatus()) {
            case CHECKMATE -> {
                ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Checkmate!");
                connectionManager.broadcast(gameID, null, message);
            }
            case STALEMATE -> {
                ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Stalemate!");
                connectionManager.broadcast(gameID, null, message);
            }
            case CHECK -> {
                ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Check!");
                connectionManager.broadcast(gameID, null, message);
            }
        }
    }

    private void validateUsername(int gameID, String authToken, String username) throws DataAccessException {
        GameData gameData = getGameData(gameID, authToken);
        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
        switch (currentTurn) {
            case WHITE -> {
                if (!Objects.equals(gameData.whiteUsername(), username)) {
                    throw new DataAccessException("Error: it is another player's turn");
                }
            }
            case BLACK -> {
                if (!Objects.equals(gameData.blackUsername(), username)) {
                    throw new DataAccessException("Error: it is another player's turn");
                }
            }
        }
    }

    private void validateGameState(ChessGame game) throws DataAccessException {
        if (game.getGameStatus() == ChessGame.GameStatus.CHECKMATE
                || game.getGameStatus() == ChessGame.GameStatus.STALEMATE
                || game.getGameStatus() == ChessGame.GameStatus.RESIGNED) {
            throw new DataAccessException("Error: game has ended");
        }
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

    private void resign(int gameID, String authToken, String username) throws DataAccessException, IOException {
        if (username == null) {
            username = gameService.getUsername(authToken);
        }
        GameData gameData = getGameData(gameID, authToken);
        ChessGame game = gameData.game();
        validateGameState(game);
        if (!Objects.equals(gameData.whiteUsername(), username) && !Objects.equals(gameData.blackUsername(), username)) {
            throw new DataAccessException("Error: cannot resign as an observer");
        }
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, null, true);
        gameService.update(updateRequest);
        String msg = username + " has resigned";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connectionManager.broadcast(gameID, null, serverMessage);
    }
}
