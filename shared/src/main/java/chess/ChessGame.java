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
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessboard;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessboard.clearBoard();
        HashSet<ChessPosition> occupiedSquares = new HashSet<>();
        occupiedSquares.addAll(board.getWhitePieceLocations());
        occupiedSquares.addAll(board.getBlackPieceLocations());
        for (ChessPosition square : occupiedSquares) {
            ChessPiece newPiece = board.getPiece(square);
            chessboard.addPiece(square, newPiece);
        }
        resetCastleStatus();
    }

    /**
     * If kings and rooks are in starting positions, castling rights are updated accordingly
     */
    private void resetCastleStatus() {
        boolean wQueenside = Castling.isCastleEnabled(chessboard, TeamColor.WHITE, Castling.CastleType.Queenside);
        boolean wKingside = Castling.isCastleEnabled(chessboard, TeamColor.WHITE, Castling.CastleType.Kingside);
        boolean bQueenside = Castling.isCastleEnabled(chessboard, TeamColor.BLACK, Castling.CastleType.Queenside);
        boolean bKingside = Castling.isCastleEnabled(chessboard, TeamColor.BLACK, Castling.CastleType.Kingside);
        chessboard.setWhiteCastlingRights(wQueenside,wKingside);
        chessboard.setBlackCastlingRights(bQueenside,bKingside);
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
        if (piece != null) {
            removeIllegalMoves(startPosition, piece, legalMoves);
            includeCastling(piece, legalMoves);
            includeEnPassant(legalMoves);
        }
        return legalMoves;
    }

    /**
     * Any moves that allow the King to be captured are omitted
     *
     * @param position Position of the piece in question
     * @param piece Piece whose moves are being checked
     * @param legalMoves List of legal moves. Begins null and legal moves are added
     */
    private void removeIllegalMoves(ChessPosition position, ChessPiece piece, HashSet<ChessMove> legalMoves) {
        ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) piece.pieceMoves(chessboard, position);
        for (ChessMove move : pieceMoves) {
            if (chessboard.moveDoesntExposeKing(piece.getTeamColor(), move)) {
                legalMoves.add(move);
            }
        }
    }

    /**
     * If piece is a King, include castling in its moves if possible
     *
     * @param piece Piece in question
     * @param moves Possible moves of the piece
     */
    private void includeCastling(ChessPiece piece, HashSet<ChessMove> moves) {
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            return;
        }
        Castling.CastlingRights castlingRights = getCastlingRights(piece.getTeamColor());
        addQueensideCastling(castlingRights.queenSide(), piece, moves);
        addKingsideCastling(castlingRights.kingSide(), piece, moves);
    }

    /**
     * Returns given team's CastlingRights object
     *
     * @param teamColor Color of team
     * @return Given team's CastlingRights object
     */
    private Castling.CastlingRights getCastlingRights(TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> chessboard.getWhiteCastlingRights();
            case BLACK -> chessboard.getBlackCastlingRights();
        };
    }

    /**
     * If possible, adds queenside castling to the piece's legal moves
     *
     * @param castleEnabled Boolean which represents queenside castling rights
     * @param piece Piece in question
     * @param moves Legal moves of given piece
     */
    private void addQueensideCastling(Boolean castleEnabled, ChessPiece piece, HashSet<ChessMove> moves) {
        TeamColor color = piece.getTeamColor();
        if (castleEnabled && Castling.canCastle(chessboard, color, Castling.CastleType.Queenside)) {
            switch (color) {
                case WHITE -> moves.add(Castling.WHITE_QUEEN_SIDE_CASTLE);
                case BLACK -> moves.add(Castling.BLACK_QUEEN_SIDE_CASTLE);
            }
        }
    }

    /**
     * If possible, adds kingside castling to the piece's legal moves
     *
     * @param castleEnabled Boolean which represents kingside castling rights
     * @param piece Piece in question
     * @param moves Legal moves of given piece
     */
    private void addKingsideCastling(Boolean castleEnabled, ChessPiece piece, HashSet<ChessMove> moves) {
        TeamColor color = piece.getTeamColor();
        if (castleEnabled && Castling.canCastle(chessboard, color, Castling.CastleType.Kingside)) {
            switch (color) {
                case WHITE -> moves.add(Castling.WHITE_KING_SIDE_CASTLE);
                case BLACK -> moves.add(Castling.BLACK_KING_SIDE_CASTLE);
            }
        }
    }

    /**
     * If possible, adds en passant to the piece's legal moves
     *
     * @param moves List of the piece's legal moves
     */
    private void includeEnPassant(HashSet<ChessMove> moves) {
        ArrayList<ChessMove> enPassantMoves = chessboard.getEnPassantMoves();
        if (enPassantMoves != null) {
            moves.addAll(enPassantMoves);
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
        if (legalMoves.contains(move) && piece.getTeamColor() == teamTurn && gameStatus == GameStatus.ONGOING) {
            chessboard.makeMove(move);
            changeTeamTurn();
            updateGameStatus();
        }
        else {
            throw new InvalidMoveException("Error: invalid move");
        }
    }

    /**
     * Flips whose turn it is
     */
    private void changeTeamTurn() {
        switch (teamTurn) {
            case WHITE -> teamTurn = TeamColor.BLACK;
            case BLACK -> teamTurn = TeamColor.WHITE;
        }
    }

    private GameStatus gameStatus = GameStatus.ONGOING;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    private void updateGameStatus() {
        if (isInCheckmate(teamTurn)) {
            gameStatus = GameStatus.CHECKMATE;
        }
        else if (isInStalemate(teamTurn)) {
            gameStatus = GameStatus.STALEMATE;
        }
    }

    public void resign() {
        gameStatus = GameStatus.RESIGNED;
    }

    public enum GameStatus {
        ONGOING,
        CHECKMATE,
        STALEMATE,
        RESIGNED
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

    /**
     * Determines if the given team has legal moves
     *
     * @param teamColor Color of team in question
     * @return True if team has no legal moves
     */
    private boolean hasNoLegalMoves(TeamColor teamColor) {
        HashSet<ChessMove> allLegalMoves = generateLegalMoves(teamColor);
        return (allLegalMoves.isEmpty());
    }

    /**
     * Returns a set of all possible moves of the given team
     *
     * @param team Color of team
     * @return Set of aggregate legal moves
     */
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
