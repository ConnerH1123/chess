package chess;

import java.util.ArrayList;

public class Castling {
    public static final ChessMove WHITE_QUEEN_SIDE_CASTLE = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3), null);
    public static final ChessMove BLACK_QUEEN_SIDE_CASTLE = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3), null);
    public static final ChessMove WHITE_KING_SIDE_CASTLE = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7), null);
    public static final ChessMove BLACK_KING_SIDE_CASTLE = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7), null);


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

    public static boolean isCastleEnabled(ChessBoard board, ChessGame.TeamColor team, CastleType side) {
        ChessPosition a1 = new ChessPosition(1,1);
        ChessPosition e1 = new ChessPosition(1,5);
        ChessPosition h1 = new ChessPosition(1,8);
        ChessPosition a8 = new ChessPosition(8,1);
        ChessPosition e8 = new ChessPosition(8,5);
        ChessPosition h8 = new ChessPosition(8,8);

        ChessPosition kingStartPos = new ChessPosition(4,4); //Temp values
        ChessPosition rookStartPos = new ChessPosition(4,4); //Temp values
        switch (team) {
            case WHITE -> {
                kingStartPos = e1;
                switch (side) {
                    case Kingside -> rookStartPos = h1;
                    case Queenside -> rookStartPos = a1;
                }

            }
            case BLACK -> {
                kingStartPos = e8;
                switch (side) {
                    case Kingside -> rookStartPos = h8;
                    case Queenside -> rookStartPos = a8;
                }
            }
        }
        ChessPiece king = board.getPiece(kingStartPos);
        ChessPiece rook = board.getPiece(rookStartPos);
        return (board.isStartingSquare(rook, rookStartPos) && board.isStartingSquare(king, kingStartPos));
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
