package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] theBoard = new ChessPiece[8][8];

    public ChessBoard() {

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

        ChessPiece[] whitePieces = {new ChessPiece(white,rook), new ChessPiece(white,knight), new ChessPiece(white,bishop), new ChessPiece(white,queen), new ChessPiece(white,king), new ChessPiece(white,bishop), new ChessPiece(white,knight), new ChessPiece(white,rook), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn), new ChessPiece(white,pawn)};
        for (int i = 0; i < 16; i++) {
            ChessPosition position = new ChessPosition((i/8)+1, (i%8)+1);
            addPiece(position, whitePieces[i]);
        }

        ChessPiece[] blackPieces = {new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,pawn), new ChessPiece(black,rook), new ChessPiece(black,knight), new ChessPiece(black,bishop), new ChessPiece(black,queen), new ChessPiece(black,king), new ChessPiece(black,bishop), new ChessPiece(black,knight), new ChessPiece(black,rook)};
        for (int i = 0; i < 16; i++) {
            ChessPosition position = new ChessPosition((i/8)+7, (i%8)+1);
            addPiece(position, blackPieces[i]);
        }
    }

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
