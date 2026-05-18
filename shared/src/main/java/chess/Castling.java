package chess;

import java.util.ArrayList;

public class Castling {
    public record CastlingRights(boolean queenSide, boolean kingSide) {
        public CastlingRights setQueenSide(boolean b) {
            return new CastlingRights(b,this.kingSide);
        }
        public CastlingRights setKingSide(boolean b) {
            return new CastlingRights(this.queenSide,b);
        }
    }

    public enum CastleType {
        Queenside,
        Kingside
    }

    public static boolean canCastle(ChessBoard theBoard, ChessGame.TeamColor team, CastleType side) {
        int row = switch (team) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int startCol = 5;
        int middleCol = 0;
        int endCol = 0;
        switch (side) {
            case Kingside -> {
                middleCol = 6;
                endCol = 7;
            }
            case Queenside -> {
                middleCol = 4;
                endCol = 3;
            }
        }
        ChessMove partialCastle = new ChessMove(new ChessPosition(row,startCol),new ChessPosition(row,middleCol),null);
        ChessMove completeCastle = new ChessMove(new ChessPosition(row,startCol),new ChessPosition(row,endCol),null);
        return (!theBoard.isInCheck(team) && isEmpty(theBoard, row,middleCol) && isEmpty(theBoard, row,endCol) &&
                theBoard.moveDoesntExposeKing(team, partialCastle) && theBoard.moveDoesntExposeKing(team, completeCastle));
    }

    private static boolean isEmpty(ChessBoard theBoard, int row, int col) {
        return theBoard.getPiece(new ChessPosition(row,col)) == null;
    }

    public static boolean castleMove(ChessBoard theBoard, ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> normalKingMoves = (ArrayList<ChessMove>) piece.pieceMoves(theBoard, move.getStartPosition());
            if (!normalKingMoves.contains(move)) {
                castle(theBoard, piece, move);
                return true;
            }
        }
        return false;
    }

    private static void castle(ChessBoard theBoard, ChessPiece piece, ChessMove move) {
        theBoard.movePiece(piece, move);

        ChessPosition endPosition = move.getEndPosition();
        int rRow = switch (piece.getTeamColor()) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int rStartingCol;
        int rEndingCol;
        switch (endPosition.getColumn()) {
            case 3 -> {
                rStartingCol = 1;
                rEndingCol = 4;
            }
            case 7 -> {
                rStartingCol = 8;
                rEndingCol = 6;
            }
            default -> {
                rStartingCol = 0;
                rEndingCol = 0;
            }
        }
        ChessPosition rStartingPosition = new ChessPosition(rRow, rStartingCol);
        ChessPosition rEndingPosition = new ChessPosition(rRow, rEndingCol);
        ChessMove rookMove = new ChessMove(rStartingPosition, rEndingPosition, null);

        theBoard.makeMove(rookMove);
    }
}
