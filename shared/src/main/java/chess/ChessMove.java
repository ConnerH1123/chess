package chess;

import java.io.PipedOutputStream;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition START_POS;
    private final ChessPosition END_POS;
    private final ChessPiece.PieceType PROMO_PIECE;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        START_POS = startPosition;
        END_POS = endPosition;
        PROMO_PIECE = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return START_POS;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return END_POS;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return PROMO_PIECE;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", START_POS, END_POS);
    }
}
