package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    private final ServerMessageType serverMessageType;
    private String message = null;
    private ChessMove move = null;
    private ChessGame game = null;
    private String errorMessage = null;
    private String username = null;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(String username, ServerMessageType type, String message) {
        this.username = username;
        if (type == ServerMessageType.ERROR) {
            this.errorMessage = message;
        }
        else {
            this.message = message;
        }
        this.serverMessageType = type;
    }

    public ServerMessage(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ServerMessage(ChessGame game, ChessMove move) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
        this.move = move;
    }


    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return this.message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ChessGame getGame() {return game;}

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {return username;}

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
