package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor COLOR;
    private final PieceType TYPE;
    private ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();

    //Array int pairs that have the horizontal and vertical transformation (i.e. [x,y])
    private final int[] UP_RIGHT_L = {1,2};
    private final int[] UP_LEFT_L = {-1,2};
    private final int[] LEFT_UP_L = {-2,1};
    private final int[] LEFT_DOWN_L = {-2,-1};
    private final int[] DOWN_LEFT_L = {-1,-2};
    private final int[] DOWN_RIGHT_L = {1,-2};
    private final int[] RIGHT_DOWN_L = {2,-1};
    private final int[] RIGHT_UP_L = {2,1};

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
        PieceType piece = board.getPiece(myPosition).getPieceType();
        possibleMoves = switch (piece) {
            case KNIGHT -> {
                ArrayList<ChessMove> knightMoves = new ArrayList<ChessMove>();
                int[][] directionList = {UP_RIGHT_L, UP_LEFT_L, LEFT_UP_L, LEFT_DOWN_L, DOWN_LEFT_L, DOWN_RIGHT_L, RIGHT_DOWN_L, RIGHT_UP_L};
                for (int[] direction : directionList) {
//                    System.out.println(board);
                    ChessPosition newPosition = getNewPosition(direction, myPosition);
                    if (isUnoccupied(board, newPosition)) { //|| isEnemyPiece(newPosition)) {
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        knightMoves.add(newMove);
                    }
                }
                yield knightMoves;
            }
            default -> {
                yield possibleMoves;
            }
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

    private boolean isUnoccupied(ChessBoard board, ChessPosition position) {
        boolean noPiece = board.getPiece(position) == null;
        return (isInBounds(position) && noPiece);
    }

    @Override
    public String toString() {
        return COLOR + " " + TYPE;
    }
}
