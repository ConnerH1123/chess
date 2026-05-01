package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor COLOR;
    private final PieceType TYPE;
    private ArrayList<ChessMove> possibleMoves = new ArrayList<>();

    //Array int pairs that have the horizontal and vertical transformation (i.e. [x,y])
    private final int[] UP = {0,1};
    private final int[] UP_LEFT_DIAG = {-1,1};
    private final int[] UP_RIGHT_DIAG = {1,1};
    private final int[] UP_RIGHT_L = {1,2};
    private final int[] UP_LEFT_L = {-1,2};
    private final int[] LEFT_UP_L = {-2,1};
    private final int[] LEFT_DOWN_L = {-2,-1};
    private final int[] DOWN_LEFT_L = {-1,-2};
    private final int[] DOWN_RIGHT_L = {1,-2};
    private final int[] RIGHT_DOWN_L = {2,-1};
    private final int[] RIGHT_UP_L = {2,1};
    private final int[] DOUBLE_UP = {0,2};

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        COLOR = pieceColor;
        TYPE = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return COLOR;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return TYPE;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        possibleMoves = switch (TYPE) {
            case KNIGHT -> {
                ArrayList<ChessMove> knightMoves = new ArrayList<>();
                int[][] directionList = {UP_RIGHT_L, UP_LEFT_L, LEFT_UP_L, LEFT_DOWN_L, DOWN_LEFT_L, DOWN_RIGHT_L, RIGHT_DOWN_L, RIGHT_UP_L};
                moveOnePerDirection(board, myPosition, directionList, knightMoves);
                yield knightMoves;
            }
            case PAWN -> {
                ArrayList<ChessMove> pawnMoves = new ArrayList<>();
                normalPawnMovement(board, myPosition, pawnMoves);
                initialPawnMovement(board, myPosition, pawnMoves);
                pawnCaptureMovement(board, myPosition, pawnMoves);
                yield pawnMoves;
            }
            default -> possibleMoves;
        };
        return possibleMoves;
    }

    private ChessPosition getNewPosition(int[] direction, ChessPosition currentPosition) {
        int xChange = direction[0];
        int yChange = direction[1];
        int newRow = currentPosition.getRow() + yChange;
        int newCol = currentPosition.getColumn() + xChange;
        return new ChessPosition(newRow, newCol);
    }

    private boolean isInBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row <= 8 && row >= 1 && col <= 8 && col >= 1);
    }

    private boolean isEmpty(ChessBoard board, ChessPosition position) {
        if (isInBounds(position)) {
            return board.getPiece(position) == null;
        }
        return false;
    }

    private boolean isEnemyPiece(ChessBoard board, ChessPosition position) {
        if (isInBounds(position)) {
            ChessPiece piece = board.getPiece(position);
            return (piece != null && piece.getTeamColor() != COLOR);
        }
        return false;
    }

    private void moveOnePerDirection(ChessBoard board, ChessPosition currentPosition, int[][] directionList, ArrayList<ChessMove> moves) {
        for (int[] direction : directionList) {
            ChessPosition newPosition = getNewPosition(direction, currentPosition);
            if (isEmpty(board, newPosition) || isEnemyPiece(board, newPosition)) {
                ChessMove newMove = new ChessMove(currentPosition, newPosition, null);
                moves.add(newMove);
            }
        }
    }

    private boolean isPromotionRank(ChessPosition position) {
        return switch (COLOR) {
            case WHITE -> position.getRow() == 8;
            case BLACK -> position.getRow() == 1;
        };
    }

    private void pawnPromotionMovement(ChessPosition currentPosition, ChessPosition newPosition, ArrayList<ChessMove> moves) {
        if (isPromotionRank(currentPosition)) {
            PieceType[] possiblePieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
            for (PieceType piece : possiblePieces) {
                ChessMove newMove = new ChessMove(currentPosition, newPosition, piece);
                moves.add(newMove);
            }
        }
        else {
            ChessMove newMove = new ChessMove(currentPosition, newPosition, null);
            moves.add(newMove);
        }
    }

    private void normalPawnMovement(ChessBoard board, ChessPosition currentPosition, ArrayList<ChessMove> moves) {
        ChessPosition newPosition = getNewPosition(UP, currentPosition);
        if (isEmpty(board, newPosition)) {
            pawnPromotionMovement(currentPosition, newPosition, moves);
        }
    }

    private boolean isInitialPawnSquare(ChessPosition position) {
        return switch (COLOR) {
            case WHITE -> position.getRow() == 2;
            case BLACK -> position.getRow() == 7;
        };
    }

    private void initialPawnMovement(ChessBoard board, ChessPosition currentPosition, ArrayList<ChessMove> moves) {
        if (isInitialPawnSquare(currentPosition)) {
            ChessPosition newPosition = getNewPosition(DOUBLE_UP, currentPosition);
            if (isEmpty(board, newPosition)) {
                ChessMove newMove = new ChessMove(currentPosition, newPosition, null);
                moves.add(newMove);
            }
        }
    }

    private void pawnCaptureMovement(ChessBoard board, ChessPosition currentPosition, ArrayList<ChessMove> moves) {
        int[][] directionList = {UP_LEFT_DIAG, UP_RIGHT_DIAG};
        for (int[] direction : directionList) {
            ChessPosition newPosition = getNewPosition(direction, currentPosition);
            if (isEnemyPiece(board, newPosition)) {
                pawnPromotionMovement(currentPosition, newPosition,moves);
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return COLOR == that.COLOR && TYPE == that.TYPE && Objects.equals(possibleMoves, that.possibleMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(COLOR, TYPE, possibleMoves);
    }

    @Override
    public String toString() {
        return COLOR + " " + TYPE;
    }
}
