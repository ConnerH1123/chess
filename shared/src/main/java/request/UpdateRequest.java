package request;

import chess.ChessGame;
import chess.ChessMove;

public record UpdateRequest(String authToken, int gameID, ChessMove move) {
}
