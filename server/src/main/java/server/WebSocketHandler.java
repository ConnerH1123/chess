package server;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import request.UpdateRequest;
import service.GameService;
import websocket.commands.UserGameCommand;

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
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), ctx.session);
                case LEAVE -> leave(command.getGameID(), ctx.session);
                case MAKE_MOVE ->
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("DEBUG: Websocket closed");
    }

    private void connect(int gameID, Session session) {
        connectionManager.add(gameID, session);
    }

    private void leave(int gameID, Session session) {
        connectionManager.remove(gameID, session);
    }

    private void move(int gameID, String authToken, ChessMove move, Session session) throws DataAccessException {
        UpdateRequest updateRequest = new UpdateRequest(authToken, gameID, move);
        gameService.update(updateRequest);
    }
}
