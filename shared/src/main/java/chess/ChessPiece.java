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

    //Array int pairs that have the horizontal and vertical transformation (i.e. [x,y])
    private final Direction UP = new Direction(0,1);
    private final Direction DOWN = new Direction(0,-1);
    private final Direction LEFT = new Direction(-1,0);
    private final Direction RIGHT = new Direction(1,0);
    private final Direction UP_LEFT_DIAG = new Direction(-1,1);
    private final Direction UP_RIGHT_DIAG = new Direction(1,1);
    private final Direction DOWN_LEFT_DIAG = new Direction(-1,-1);
    private final Direction DOWN_RIGHT_DIAG = new Direction(1,-1);
    private final Direction UP_RIGHT_L = new Direction(1,2);
    private final Direction UP_LEFT_L = new Direction(-1,2);
    private final Direction LEFT_UP_L = new Direction(-2,1);
    private final Direction LEFT_DOWN_L = new Direction(-2,-1);
    private final Direction DOWN_LEFT_L = new Direction(-1,-2);
    private final Direction DOWN_RIGHT_L = new Direction(1,-2);
    private final Direction RIGHT_DOWN_L = new Direction(2,-1);
    private final Direction RIGHT_UP_L = new Direction(2,1);

    private record Direction(int x, int y) {}

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
        ArrayList<ChessMove> possibleMoves = switch (TYPE) {
            case KING -> {
                ArrayList<ChessMove> kingMoves = new ArrayList<>();
                Direction[] directionList = {UP, DOWN, LEFT, RIGHT, UP_LEFT_DIAG, UP_RIGHT_DIAG, DOWN_RIGHT_DIAG, DOWN_LEFT_DIAG};
                moveOnePerDirection(board, myPosition, directionList, kingMoves);
                yield kingMoves;
            }
            case QUEEN -> {
                ArrayList<ChessMove> queenMoves = new ArrayList<>();
                Direction[] directionList = {UP, DOWN, LEFT, RIGHT, UP_LEFT_DIAG, UP_RIGHT_DIAG, DOWN_RIGHT_DIAG, DOWN_LEFT_DIAG};
                moveUntilStopped(board, myPosition, directionList, queenMoves);
                yield queenMoves;
            }
            case ROOK -> {
                ArrayList<ChessMove> rookMoves = new ArrayList<>();
                Direction[] directionList = {UP, DOWN, LEFT, RIGHT};
                moveUntilStopped(board, myPosition, directionList, rookMoves);
                yield rookMoves;
            }
            case BISHOP -> {
                ArrayList<ChessMove> bishopMoves = new ArrayList<>();
                Direction[] directionList = {UP_LEFT_DIAG, UP_RIGHT_DIAG, DOWN_RIGHT_DIAG, DOWN_LEFT_DIAG};
                moveUntilStopped(board, myPosition, directionList, bishopMoves);
                yield bishopMoves;
            }
            case KNIGHT -> {
                ArrayList<ChessMove> knightMoves = new ArrayList<>();
                Direction[] directionList = {UP_RIGHT_L, UP_LEFT_L, LEFT_UP_L, LEFT_DOWN_L, DOWN_LEFT_L, DOWN_RIGHT_L, RIGHT_DOWN_L, RIGHT_UP_L};
                moveOnePerDirection(board, myPosition, directionList, knightMoves);
                yield knightMoves;
            }
            case PAWN -> {
                ArrayList<ChessMove> pawnMoves = new ArrayList<>();
                Direction direction = getPawnDirection();
                normalPawnMovement(board, myPosition, direction, pawnMoves);
                initialPawnMovement(board, myPosition, direction, pawnMoves);
                pawnCaptureMovement(board, myPosition, pawnMoves);
                yield pawnMoves;
            }
        };
        return possibleMoves;
    }

    private ChessPosition getNewPosition(Direction direction, ChessPosition currentPosition) {
        int xChange = direction.x();
        int yChange = direction.y();
        int newRow = currentPosition.getRow() + yChange;
        int newCol = currentPosition.getColumn() + xChange;
        return new ChessPosition(newRow, newCol);
    }

    /**
     * Returns true if position is within the board limits
     */
    private boolean isInBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row <= 8 && row >= 1 && col <= 8 && col >= 1);
    }

    /**
     * Returns true if given square is empty
     */
    private boolean isEmpty(ChessBoard board, ChessPosition position) {
        if (isInBounds(position)) {
            return board.getPiece(position) == null;
        }
        return false;
    }

    /**
     * Returns true if given square contains enemy piece
     */
    private boolean isEnemyPiece(ChessBoard board, ChessPosition position) {
        if (isInBounds(position)) {
            ChessPiece piece = board.getPiece(position);
            return (piece != null && piece.getTeamColor() != COLOR);
        }
        return false;
    }

    /**
     * Given an array of directions, updates possible moves to include one step per direction (if possible)
     */
    private void moveOnePerDirection(ChessBoard board, ChessPosition currentPosition, Direction[] directionList, ArrayList<ChessMove> moves) {
        for (Direction direction : directionList) {
            ChessPosition newPosition = getNewPosition(direction, currentPosition);
            if (isEmpty(board, newPosition) || isEnemyPiece(board, newPosition)) {
                ChessMove newMove = new ChessMove(currentPosition, newPosition, null);
                moves.add(newMove);
            }
        }
    }

    /**
     * Helper rec for moveUntilStopped
     */
    private void moveUntilStoppedRec(ChessBoard board, ChessPosition OGposition, ChessPosition currentPosition, Direction direction, ArrayList<ChessMove> moves) {
        ChessPosition newPosition = getNewPosition(direction, currentPosition);
        if (isEnemyPiece(board, newPosition)) {
            ChessMove newMove = new ChessMove(OGposition, newPosition, null);
            moves.add(newMove);
        }
        else if (isEmpty(board, newPosition)) {
            ChessMove newMove = new ChessMove(OGposition, newPosition, null);
            moves.add(newMove);
            moveUntilStoppedRec(board, OGposition, newPosition, direction, moves);
        }
    }

    /**
     * Given an array of directions, updates moves to include all possible movements per direction
     */
    private void moveUntilStopped(ChessBoard board, ChessPosition position, Direction[] directionList, ArrayList<ChessMove> moves) {
        for (Direction direction : directionList) {
            moveUntilStoppedRec(board, position, position, direction, moves);
        }
    }

    /**
     * e.g. if PAWN is BLACK direction is DOWN
     */
    private Direction getPawnDirection() {
        return switch (COLOR) {
            case WHITE -> UP;
            case BLACK -> DOWN;
        };
    }

    /**
     * e.g. returns true if PAWN is BLACK and on the 1st rank
     */
    private boolean isPromotionRank(ChessPosition position) {
        return switch (COLOR) {
            case WHITE -> position.getRow() == 8;
            case BLACK -> position.getRow() == 1;
        };
    }

    /**
     * Given a valid pawn movement, will promote pawn if applicable
     */
    private void pawnPromotionMovement(ChessPosition currentPosition, ChessPosition newPosition, ArrayList<ChessMove> moves) {
        if (isPromotionRank(newPosition)) {
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

    /**
     * Adds forward pawn movement to movement list
     */
    private void normalPawnMovement(ChessBoard board, ChessPosition currentPosition, Direction direction, ArrayList<ChessMove> moves) {
        ChessPosition newPosition = getNewPosition(direction, currentPosition);
        if (isEmpty(board, newPosition)) {
            pawnPromotionMovement(currentPosition, newPosition, moves);
        }
    }

    /**
     * Returns true if PAWN is BLACK and on the 7th rank
     */
    private boolean isInitialPawnSquare(ChessPosition position) {
        return switch (COLOR) {
            case WHITE -> position.getRow() == 2;
            case BLACK -> position.getRow() == 7;
        };
    }

    /**
     * Adds a double forward pawn movement if applicable
     */
    private void initialPawnMovement(ChessBoard board, ChessPosition currentPosition, Direction direction, ArrayList<ChessMove> moves) {
        if (isInitialPawnSquare(currentPosition)) {
            ChessPosition tempPosition = getNewPosition(direction, currentPosition);
            ChessPosition newPosition = getNewPosition(direction, tempPosition);
            if (isEmpty(board, tempPosition) && isEmpty(board, newPosition)) {
                ChessMove newMove = new ChessMove(currentPosition, newPosition, null);
                moves.add(newMove);
            }
        }
    }

    /**
     * Adds a diagonal pawn movement if applicable
     */
    private void pawnCaptureMovement(ChessBoard board, ChessPosition currentPosition, ArrayList<ChessMove> moves) {
        Direction[] directionList = switch (COLOR) {
            case WHITE -> new Direction[]{UP_LEFT_DIAG, UP_RIGHT_DIAG};
            case BLACK -> new Direction[]{DOWN_LEFT_DIAG, DOWN_RIGHT_DIAG};
        };
        for (Direction direction : directionList) {
            ChessPosition newPosition = getNewPosition(direction, currentPosition);
            if (isEnemyPiece(board, newPosition)) {
                pawnPromotionMovement(currentPosition, newPosition, moves);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return COLOR == that.COLOR && TYPE == that.TYPE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(COLOR, TYPE);
    }

    @Override
    public String toString() {
        return COLOR + " " + TYPE;
    }
}
