package websocket;
import chess.ChessMove;
import client.ResponseException;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private final Session session;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    websocket.messages.ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(int gameID, String username) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, null, gameID, username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID, String username) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID, String username) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, username);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, String username, ChessMove move) throws ResponseException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, username, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
}
