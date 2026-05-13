package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
        teamTurn = TeamColor.WHITE;
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
        removeIllegalMoves(startPosition, piece, legalMoves);
        includeCastling(piece, legalMoves);
        //includeEnPassant(piece, legalMoves);
        return legalMoves;
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

    private void includeCastling(ChessPiece piece, HashSet<ChessMove> moves) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            return;
        }
        ChessBoard.CastlingRights castlingRights = getCastlingRights(piece.getTeamColor());
        if (castlingRights.queenSide()) {
            addQueenSideCastling(piece, moves);
        }
        //if (castlingRights.getKingSide()) {
            //addKingSideCastling(piece, moves);
        //}
    }

    ChessBoard.CastlingRights getCastlingRights(TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> chessboard.getWhiteCastlingRights();
            case BLACK -> chessboard.getBlackCastlingRights();
        };
    }

    private final ChessMove WHITE_QUEEN_SIDE_CASTLE = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,3), null);
    private final ChessMove BLACK_QUEEN_SIDE_CASTLE = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,3), null);
    private final ChessMove WHITE_KING_SIDE_CASTLE = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,7), null);
    private final ChessMove BLACK_KING_SIDE_CASTLE = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,7), null);

    private void addQueenSideCastling(ChessPiece piece, HashSet<ChessMove> moves) {
        if (piece.getTeamColor() == TeamColor.WHITE && chessboard.whiteQueenSideCastle()) {
            moves.add(WHITE_QUEEN_SIDE_CASTLE);
        }
        else if (piece.getTeamColor() == TeamColor.BLACK && chessboard.blackQueenSideCastle()) {
            moves.add(BLACK_QUEEN_SIDE_CASTLE);
        }
        else if (piece.getTeamColor() == TeamColor.WHITE && chessboard.whiteKingSideCastle()) {
            moves.add(WHITE_KING_SIDE_CASTLE);
        }
        else if (piece.getTeamColor() == TeamColor.BLACK && chessboard.blackKingSideCastle()) {
            moves.add(BLACK_KING_SIDE_CASTLE);
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        HashSet<ChessMove> legalMoves = (HashSet<ChessMove>) validMoves(move.getStartPosition());
        ChessPiece piece = chessboard.getPiece(move.getStartPosition());
        if (legalMoves.contains(move) && piece.getTeamColor() == teamTurn) {
            chessboard.makeMove(move);
            changeTeamTurn();
        }
        else {
            throw new InvalidMoveException();
        }
    }

    private void changeTeamTurn() {
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return chessboard.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (isInCheck(teamColor) && hasNoLegalMoves(teamColor));
    }

    private boolean hasNoLegalMoves(TeamColor teamColor) {
        HashSet<ChessMove> allLegalMoves = generateLegalMoves(teamColor);
        return (allLegalMoves.isEmpty());
    }

    private HashSet<ChessMove> generateLegalMoves(TeamColor team) {
        HashSet<ChessMove> moves = new HashSet<>();
        HashSet<ChessPosition> piecePositions = chessboard.getFriendlyLocations(team);
        for (ChessPosition position : piecePositions) {
            moves.addAll(validMoves(position));
        }
        return moves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor) && hasNoLegalMoves(teamColor));
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessboard.clearBoard();
        HashSet<ChessPosition> whiteOccupiedSquares = board.getWhitePieceLocations();
        for (ChessPosition occupiedSquare : whiteOccupiedSquares) {
            ChessPiece newPiece = board.getPiece(occupiedSquare);
            chessboard.addPiece(occupiedSquare, newPiece);
        }
        HashSet<ChessPosition> blackOccupiedSquares = board.getBlackPieceLocations();
        for (ChessPosition occupiedSquare : blackOccupiedSquares) {
            ChessPiece newPiece = board.getPiece(occupiedSquare);
            chessboard.addPiece(occupiedSquare, newPiece);
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessboard;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(chessboard, chessGame.getBoard()) && teamTurn == chessGame.getTeamTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessboard, teamTurn);
    }

    @Override
    public String toString() {
        return chessboard + "\nTurn: " + teamTurn;
    }
}
