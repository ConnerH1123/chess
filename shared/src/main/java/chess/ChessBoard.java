package chess;

import java.util.*;

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

    private final HashSet<ChessPosition> whitePieceLocations = new HashSet<>();
    private final HashSet<ChessPosition> blackPieceLocations = new HashSet<>();

    private ChessPosition whiteKingLocation;
    private ChessPosition blackKingLocation;

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
        updatePieceCount(piece, true);
        updatePieceLocation(piece,position,true);
        updateKingPosition(piece, position);
    }

    private void updatePieceCount(ChessPiece piece, boolean isAdded) {
        if (piece == null) {
            return;
        }
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

    private void updatePieceLocation(ChessPiece piece, ChessPosition position, boolean isAdded) {
        if (piece == null) {
            return;
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        if (isAdded) {
            if (color == ChessGame.TeamColor.WHITE) {
                whitePieceLocations.add(position);
            }
            else {
                blackPieceLocations.add(position);
            }
        }
        else {
            if (color == ChessGame.TeamColor.WHITE) {
                whitePieceLocations.remove(position);
            }
            else {
                blackPieceLocations.remove(position);
            }
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

        clearBoard();
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
        blackCastlingRights = new CastlingRights(true,true);
        whiteCastlingRights = new CastlingRights(true,true);
    }

    public record CastlingRights(boolean queenSide, boolean kingSide) {
        public CastlingRights setQueenSide(boolean b) {
            return new CastlingRights(b,this.kingSide);
        }
        public CastlingRights setKingSide(boolean b) {
            return new CastlingRights(this.queenSide,b);
        }
    }

    private CastlingRights whiteCastlingRights = new CastlingRights(false,false);
    private CastlingRights blackCastlingRights = new CastlingRights(false,false);

    public CastlingRights getBlackCastlingRights() {
        return blackCastlingRights;
    }

    public CastlingRights getWhiteCastlingRights() {
        return whiteCastlingRights;
    }

    public void setBlackCastlingRights(boolean queenSide, boolean kingSide) {
        blackCastlingRights = blackCastlingRights.setQueenSide(queenSide);
        blackCastlingRights = blackCastlingRights.setKingSide(kingSide);
    }

    public void setWhiteCastlingRights(boolean queenSide, boolean kingSide) {
        whiteCastlingRights = whiteCastlingRights.setQueenSide(queenSide);
        whiteCastlingRights = whiteCastlingRights.setKingSide(kingSide);
    }


    private CastlingRights getCastlingRights(ChessGame.TeamColor teamColor) {
        return switch (teamColor) {
            case WHITE -> whiteCastlingRights;
            case BLACK -> blackCastlingRights;
        };
    }

    public boolean whiteKingSideCastle() {
        if (isInCheck(ChessGame.TeamColor.WHITE) || !isEmptyPosition(1,6) || !isEmptyPosition(1,7)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,6),null);
        copy.makeMove(partialCastle);
        if (isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(1,6),new ChessPosition(1,7),null);
        copy.makeMove(completeCastle);
        if (isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        return true;
    }

    public boolean whiteQueenSideCastle() {
        if (isInCheck(ChessGame.TeamColor.WHITE) || !isEmptyPosition(1,4) || !isEmptyPosition(1,3)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,4),null);
        copy.makeMove(partialCastle);
        if (isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(1,4),new ChessPosition(1,3),null);
        copy.makeMove(completeCastle);
        if (isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        return true;
    }


    public boolean blackKingSideCastle() {
        if (isInCheck(ChessGame.TeamColor.BLACK) || !isEmptyPosition(8,6) || !isEmptyPosition(8,7)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,6),null);
        copy.makeMove(partialCastle);
        if (isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(8,6),new ChessPosition(8,7),null);
        copy.makeMove(completeCastle);
        if (isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        return true;
    }

    public boolean blackQueenSideCastle() {
        if (isInCheck(ChessGame.TeamColor.BLACK) || !isEmptyPosition(8,4) || !isEmptyPosition(8,3)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,4),null);
        copy.makeMove(partialCastle);
        if (isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(8,4),new ChessPosition(8,3),null);
        copy.makeMove(completeCastle);
        if (isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        return true;
    }


    private boolean isEmptyPosition(int row, int col) {
        return getPiece(new ChessPosition(row,col)) == null;
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
        if (movingPiece == null) {
            return;
        }
        ChessPiece capturedPiece = getPiece(endPosition);
        if (move.getPromotionPiece() != null) {
            updatePieceCount(movingPiece, false);
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            updatePieceCount(movingPiece, true);
        }
        if (capturedPiece != null) {
            updatePieceCount(capturedPiece, false);
            updatePieceLocation(capturedPiece,endPosition,false);
        }
        updatePieceLocation(movingPiece,startPosition, false);
        updatePieceLocation(movingPiece, endPosition, true);
        updateKingPosition(movingPiece, endPosition);
        updateCastleStatus(movingPiece, startPosition);
        //updateEnPassantStatus(movingPiece, move);

        if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> normalKingMoves = (ArrayList<ChessMove>) movingPiece.pieceMoves(this, startPosition);
            if (!normalKingMoves.contains(move)) {
                castle(movingPiece, move);
            }
        }

        theBoard[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
        theBoard[endPosition.getRow()-1][endPosition.getColumn()-1] = movingPiece;
    }

    private void castle(ChessPiece piece, ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        theBoard[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
        theBoard[endPosition.getRow()-1][endPosition.getColumn()-1] = piece;

        int rRow = switch (piece.getTeamColor()) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int rStartingCol = switch (endPosition.getColumn()) {
            case 3 -> 1;
            case 7 -> 8;
            default -> 0;
        };
        int rEndingCol = switch (endPosition.getColumn()) {
            case 3 -> 4;
            case 7 -> 6;
            default -> 0;
        };
        ChessPosition rStartingPosition = new ChessPosition(rRow, rStartingCol);
        ChessPosition rEndingPosition = new ChessPosition(rRow, rEndingCol);
        ChessMove rookMove = new ChessMove(rStartingPosition, rEndingPosition, null);

        makeMove(rookMove);
    }

    private void updateKingPosition(ChessPiece piece, ChessPosition position) {
        if (piece == null) {
            return;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                whiteKingLocation = position;
            }
            else {
                blackKingLocation = position;
            }
        }
    }

    private void updateCastleStatus(ChessPiece piece, ChessPosition position) {
        CastlingRights castlingRights = getCastlingRights(piece.getTeamColor());
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            castlingRights = castlingRights.setQueenSide(false);
            castlingRights = castlingRights.setKingSide(false);
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK && isStartingSquare(piece, position) && position.getColumn() == 1) {
            castlingRights = castlingRights.setQueenSide(false);
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK && isStartingSquare(piece, position) && position.getColumn() == 8) {
            castlingRights = castlingRights.setKingSide(false);
        }
    }

    public boolean isStartingSquare(ChessPiece piece, ChessPosition position) {
        if (piece == null) {
            return false;
        }
        int row = switch (piece.getTeamColor()) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int col1 = switch (piece.getPieceType()) {
            case KING -> 5;
            case QUEEN -> 4;
            case BISHOP -> 3;
            case KNIGHT -> 2;
            case ROOK -> 1;
            case PAWN -> 0;
        };
        int col2 = switch (piece.getPieceType()) {
            case KING -> 5;
            case QUEEN -> 4;
            case BISHOP -> 6;
            case KNIGHT -> 7;
            case ROOK -> 8;
            case PAWN -> 0;
        };
        return (position.equals(new ChessPosition(row,col1)) || position.equals(new ChessPosition(row,col2)));
    }

    public boolean isInCheck(ChessGame.TeamColor team) {
        HashSet<ChessPosition> enemyPieceLocations = getEnemyLocations(team);
        ChessPosition kingPosition = getKingPosition(team);
        for (ChessPosition enemySquare : enemyPieceLocations) {
            ChessPiece enemyPiece = getPiece(enemySquare);
            ArrayList<ChessMove> pieceMoves = (ArrayList<ChessMove>) enemyPiece.pieceMoves(this, enemySquare);
            for (ChessMove move : pieceMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private HashSet<ChessPosition> getEnemyLocations(ChessGame.TeamColor team) {
        return switch (team) {
            case WHITE -> blackPieceLocations;
            case BLACK -> whitePieceLocations;
        };
    }

    public HashSet<ChessPosition> getFriendlyLocations(ChessGame.TeamColor team) {
        return switch (team) {
            case WHITE -> whitePieceLocations;
            case BLACK -> blackPieceLocations;
        };
    }


    private ChessPosition getKingPosition(ChessGame.TeamColor team) {
        return switch (team) {
            case WHITE -> whiteKingLocation;
            case BLACK -> blackKingLocation;
        };
    }

    public void clearBoard() {
        clearPieces();
        clearLocations();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                theBoard[i][j] = null;
            }
        }
    }

    private void clearPieces() {
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            whitePieces.put(type, 0);
            blackPieces.put(type, 0);
        }
    }

    private void clearLocations() {
        whitePieceLocations.clear();
        blackPieceLocations.clear();
        whiteKingLocation = null;
        blackKingLocation = null;
    }

    public HashSet<ChessPosition> getWhitePieceLocations() {
        return whitePieceLocations;
    }

    public HashSet<ChessPosition> getBlackPieceLocations() {
        return blackPieceLocations;
    }

    public ChessPosition getWhiteKingLocation() {
        return whiteKingLocation;
    }

    public ChessPosition getBlackKingLocation() {
        return blackKingLocation;
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
                if (theBoard[i][j] != null) {
                    sb.append(theBoard[i][j]);
                }
                else {
                    sb.append("    ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
