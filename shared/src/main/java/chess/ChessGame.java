package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final ChessBoard chessboard = new ChessBoard();
    private TeamColor teamTurn;

    public ChessGame() {
        chessboard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        ChessPiece piece = chessboard.getPiece(startPosition);
        if (piece == null) {
            return legalMoves;
        }
        //removeIllegalMoves(startPosition, piece, legalMoves);
        //includeCastling(piece, legalMoves);
        //includeEnPassant(piece, legalMoves);
        //return legalMoves;
        throw new RuntimeException("Not implemented");
    }

    private void removeIllegalMoves(ChessPosition position, ChessPiece piece, HashSet<ChessMove> legalMoves) {
        ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(chessboard, position);
        for (ChessMove move : pieceMoves) {
            ChessBoard boardCopy = chessboard.copyBoard();
            boardCopy.makeMove(move);
            if (!boardCopy.isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //if not move in allLegalMoves throw InvalidMoveException;
        //else chessboard.makeMove(move);
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //return chessboard.isInCheck(teamColor);
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //return (isInCheck(teamColor) && !hasLegalMoves(teamColor));
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        //chessboard.clearBoard();
        //for (ChessPosition occupiedSquare : HashSet<ChessPosition> whiteOccupiedSquares) {
            //ChessPiece newPiece = board.getPiece();
            //chessboard.addPiece(occupiedSquare, newPiece);
        //}
        //for (ChessPosition occupiedSquare : HashSet<ChessPosition> blackOccupiedSquares) {
            //ChessPiece newPiece = board.getPiece();
            //chessboard.addPiece(occupiedSquare, newPiece);
        //}
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessboard;
    }
}
