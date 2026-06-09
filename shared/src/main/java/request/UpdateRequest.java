package request;

import chess.ChessMove;

public record UpdateRequest(String authToken, int gameID, ChessMove move, boolean resignStatus) {}
