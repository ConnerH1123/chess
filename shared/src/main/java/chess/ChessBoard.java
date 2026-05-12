package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] theBoard = new ChessPiece[8][8];

    private final HashMap<ChessPiece.PieceType,Integer> whitePieces = new HashMap<>();
    private final HashMap<ChessPiece.PieceType,Integer> blackPieces = new HashMap<>();


    public ChessBoard() {
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            whitePieces.put(type, 0);
            blackPieces.put(type, 0);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int column = position.getColumn();
        theBoard[row-1][column-1] = piece;
        updatePieceMap(piece, true);
    }

    private void updatePieceMap(ChessPiece piece, boolean isAdded) {
        int increment;
        if (isAdded) {
            increment = 1;
        }
        else {
            increment = -1;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            whitePieces.merge(piece.getPieceType(), increment, Integer::sum);
        }
        else {
            blackPieces.merge(piece.getPieceType(), increment, Integer::sum);
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return theBoard[row-1][column-1];
    }

    public HashMap<ChessPiece.PieceType, Integer> getWhitePieces() {
        return whitePieces;
    }

    public HashMap<ChessPiece.PieceType, Integer> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        ChessPiece.PieceType king = ChessPiece.PieceType.KING;
        ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;
        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
        ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;

        ChessPiece wKing = new ChessPiece(white, king);
        ChessPiece wQueen = new ChessPiece(white, queen);
        ChessPiece wRook = new ChessPiece(white, rook);
        ChessPiece wBishop = new ChessPiece(white, bishop);
        ChessPiece wKnight = new ChessPiece(white, knight);
        ChessPiece wPawn = new ChessPiece(white, pawn);
        ChessPiece bKing = new ChessPiece(black, king);
        ChessPiece bQueen = new ChessPiece(black, queen);
        ChessPiece bRook = new ChessPiece(black, rook);
        ChessPiece bBishop = new ChessPiece(black, bishop);
        ChessPiece bKnight = new ChessPiece(black, knight);
        ChessPiece bPawn = new ChessPiece(black, pawn);

        ChessPiece[] rank1 = {wRook, wKnight, wBishop, wQueen, wKing, wBishop, wKnight, wRook};
        ChessPiece[] rank8 = {bRook, bKnight, bBishop, bQueen, bKing, bBishop, bKnight, bRook};

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition newPosition = new ChessPosition(i+1,j+1);
                if (i == 0) {
                    addPiece(newPosition, rank1[j]);
                }
                else if (i == 1) {
                    addPiece(newPosition, wPawn);
                }
                else if (i == 6) {
                    addPiece(newPosition, bPawn);
                }
                else if (i == 7) {
                    addPiece(newPosition, rank8[j]);
                }
                else {
                    theBoard[i][j] = null;
                }
            }
        }
    }

    public ChessBoard copyBoard() {
        ChessBoard copy = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i+1,j+1);
                copy.addPiece(position, theBoard[i][j]);
            }
        }
        return copy;
    }

    public void makeMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece movingPiece = getPiece(startPosition);
        ChessPiece capturedPiece = getPiece(endPosition);
        if (move.getPromotionPiece() != null) {
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }
        if (capturedPiece != null) {
            updatePieceMap(capturedPiece, false);
        }

        theBoard[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
        theBoard[endPosition.getRow()-1][endPosition.getColumn()-1] = movingPiece;
    }

    public boolean isInCheck(ChessGame.TeamColor team) {
        //HashSet<ChessPosition> enemyPieceLocations;
        //ChessPosition kingPosition = getKingPosition(team);
        //for (ChessPosition enemySquare : enemyPieceLocations) {
            //ChessPiece enemyPiece = getPiece(enemySquare);
            //ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) enemyPiece.pieceMoves(chessboard, position);
            //for (ChessMove move = pieceMoves) {
                //if (move.getEndPosition() == kingPosition) {
                    //return true;
                //}
            //}
        //}
        return false;
    }

    //private ChessPosition getKingPosition(ChessGame.TeamColor team) {
        //ChessPosition kingPosition = switch (team) {
            //case WHITE -> {
                //enemyPieceLocations = blackPieceLocations;
                //yield whiteKingPosition;
            //}
            //case BLACK -> {
                //enemyPieceLocations = whitePieceLocations;
                //yield blackKingPosition;
            //}
        //};
        //return kingPosition;
    //}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(theBoard, that.theBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(theBoard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                sb.append("|");
                sb.append(theBoard[i][j]);
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
