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
    private final String message;
    private final ChessMove move;
    private final ChessGame game;
    private String errorMessage = null;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        if (type == ServerMessageType.ERROR) {
            errorMessage = message;
            this.message = null;
        }
        else {
            this.message = message;
        }
        this.serverMessageType = type;
        this.move = null;
        this.game = null;
    }

    public ServerMessage(ServerMessageType type, String message, ChessGame game) {
        this.serverMessageType = type;
        this.message = message;
        this.game = game;
        this.move = null;
    }

    public ServerMessage(ServerMessageType type, String message, ChessMove move) {
        this.serverMessageType = type;
        this.message = message;
        this.move = move;
        this.game = null;
    }


    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return this.message;
    }

    public ChessMove getMove() {
        return move;
    }

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
