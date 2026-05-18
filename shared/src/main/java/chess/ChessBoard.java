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
        if (!isInBounds(position)) {
            return null;
        }
        int row = position.getRow();
        int column = position.getColumn();
        return theBoard[row-1][column-1];
    }

    private boolean isInBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row <= 8 && row >= 1 && col <= 8 && col >= 1);
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
        ChessPiece[] rank1 = {ValidChessPiece.wRook, ValidChessPiece.wKnight, ValidChessPiece.wBishop, ValidChessPiece.wQueen, ValidChessPiece.wKing, ValidChessPiece.wBishop, ValidChessPiece.wKnight, ValidChessPiece.wRook};
        ChessPiece[] rank8 = {ValidChessPiece.bRook, ValidChessPiece.bKnight, ValidChessPiece.bBishop, ValidChessPiece.bQueen, ValidChessPiece.bKing, ValidChessPiece.bBishop, ValidChessPiece.bKnight, ValidChessPiece.bRook};

        clearBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition newPosition = new ChessPosition(i+1,j+1);
                switch (i) {
                    case 0 -> addPiece(newPosition, rank1[j]);
                    case 1 -> addPiece(newPosition, ValidChessPiece.wPawn);
                    case 6 -> addPiece(newPosition, ValidChessPiece.bPawn);
                    case 7 -> addPiece(newPosition, rank8[j]);
                    default -> theBoard[i][j] = null;
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

    public boolean whiteKingSideCastle() {
        if (isInCheck(ChessGame.TeamColor.WHITE) || isOccupied(1,6) || isOccupied(1,7)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,6),null);
        copy.makeMove(partialCastle);
        if (copy.isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(1,6),new ChessPosition(1,7),null);
        copy.makeMove(completeCastle);
        if (copy.isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        return true;
    }

    public boolean whiteQueenSideCastle() {
        if (isInCheck(ChessGame.TeamColor.WHITE) || isOccupied(1,4) || isOccupied(1,3)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(1,5),new ChessPosition(1,4),null);
        copy.makeMove(partialCastle);
        if (copy.isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(1,4),new ChessPosition(1,3),null);
        copy.makeMove(completeCastle);
        if (copy.isInCheck(ChessGame.TeamColor.WHITE)) {
            return false;
        }
        return true;
    }


    public boolean blackKingSideCastle() {
        if (isInCheck(ChessGame.TeamColor.BLACK) || isOccupied(8,6) || isOccupied(8,7)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,6),null);
        copy.makeMove(partialCastle);
        if (copy.isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(8,6),new ChessPosition(8,7),null);
        copy.makeMove(completeCastle);
        if (copy.isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        return true;
    }

    public boolean blackQueenSideCastle() {
        if (isInCheck(ChessGame.TeamColor.BLACK) || isOccupied(8,4) || isOccupied(8,3)) {
            return false;
        }
        ChessBoard copy = copyBoard();
        ChessMove partialCastle = new ChessMove(new ChessPosition(8,5),new ChessPosition(8,4),null);
        copy.makeMove(partialCastle);
        if (copy.isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        ChessMove completeCastle = new ChessMove(new ChessPosition(8,4),new ChessPosition(8,3),null);
        copy.makeMove(completeCastle);
        if (copy.isInCheck(ChessGame.TeamColor.BLACK)) {
            return false;
        }
        return true;
    }


    private boolean isOccupied(int row, int col) {
        return getPiece(new ChessPosition(row,col)) != null;
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

    private final ArrayList<ChessMove> enPassantMoveList = new ArrayList<>();

    public ArrayList<ChessMove> getEnPassantMoves() {
        return enPassantMoveList;
    }

    public void makeMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        if (getPiece(startPosition) == null) {
            return;
        }
        enPassantMoveList.clear();
        boolean pieceCaptured = capturePiece(endPosition);
        ChessPiece movingPiece = promotionMove(move);
        boolean castled = castleMove(movingPiece, move);
        boolean didEnPassant = enPassant(movingPiece, move, pieceCaptured);
        if (!castled && !didEnPassant) {
            movePiece(movingPiece, move);
        }
    }

    private boolean castleMove(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> normalKingMoves = (ArrayList<ChessMove>) piece.pieceMoves(this, move.getStartPosition());
            if (!normalKingMoves.contains(move)) {
                castle(piece, move);
                return true;
            }
        }
        return false;
    }

    private boolean enPassant(ChessPiece piece, ChessMove move, boolean pieceCaptured ) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ArrayList<ChessMove> normalPawnMoves = (ArrayList<ChessMove>) piece.pieceMoves(this, move.getStartPosition());
            if (!normalPawnMoves.contains(move) && !pieceCaptured) {
                enPassantMove(move);
                return true;
            }
        }
        return false;
    }

    private ChessPiece promotionMove(ChessMove move) {
        ChessPiece piece = getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            updatePieceCount(piece, false);
            updatePieceCount(piece, true);
            return new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        else {
            return piece;
        }
    }

    private boolean capturePiece(ChessPosition position) {
        ChessPiece capturedPiece = getPiece(position);
        if (capturedPiece != null) {
            updatePieceCount(capturedPiece, false);
            updatePieceLocation(capturedPiece,position,false);
            return true;
        }
        return false;
    }

    private void movePiece(ChessPiece piece, ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        updatePieceLocation(piece,startPosition, false);
        updatePieceLocation(piece, endPosition, true);
        updateKingPosition(piece, endPosition);
        updateCastleStatus(piece, startPosition);
        updateEnPassantStatus(piece, move);

        theBoard[startPosition.getRow()-1][startPosition.getColumn()-1] = null;
        theBoard[endPosition.getRow()-1][endPosition.getColumn()-1] = piece;
    }

    private void castle(ChessPiece piece, ChessMove move) {
        movePiece(piece, move);

        ChessPosition endPosition = move.getEndPosition();
        int rRow = switch (piece.getTeamColor()) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int rStartingCol;
        int rEndingCol;
        switch (endPosition.getColumn()) {
            case 3 -> {
                rStartingCol = 1;
                rEndingCol = 4;
            }
            case 7 -> {
                rStartingCol = 8;
                rEndingCol = 6;
            }
            default -> {
                rStartingCol = 0;
                rEndingCol = 0;
            }
        }
        ChessPosition rStartingPosition = new ChessPosition(rRow, rStartingCol);
        ChessPosition rEndingPosition = new ChessPosition(rRow, rEndingCol);
        ChessMove rookMove = new ChessMove(rStartingPosition, rEndingPosition, null);

        makeMove(rookMove);
    }

    private void updateEnPassantStatus(ChessPiece piece, ChessMove move) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return;
        }

        int startRow = move.getStartPosition().getRow();
        int endRow = move.getEndPosition().getRow();
        if (Math.abs(startRow-endRow) != 2) {
            return;
        }

        ChessPosition leftAdjacentPosition = new ChessPosition(endRow, move.getEndPosition().getColumn()-1);
        ChessPosition rightAdjacentPosition = new ChessPosition(endRow, move.getEndPosition().getColumn()+1);
        ChessPiece leftAdjacentPiece = getPiece(leftAdjacentPosition);
        ChessPiece rightAdjacentPiece = getPiece(rightAdjacentPosition);
        ChessPosition behindPawn = new ChessPosition((startRow+endRow)/2, move.getEndPosition().getColumn());

        if (leftAdjacentPiece != null && leftAdjacentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessMove enPassant = new ChessMove(leftAdjacentPosition, behindPawn, null);
            enPassantMoveList.add(enPassant);
        }
        if (rightAdjacentPiece != null && rightAdjacentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessMove enPassant = new ChessMove(rightAdjacentPosition, behindPawn, null);
            enPassantMoveList.add(enPassant);
        }

    }

    private void enPassantMove(ChessMove move) {
        int row = move.getStartPosition().getRow();
        int col = move.getEndPosition().getColumn();
        ChessPosition partialEndPosition = new ChessPosition(row, col);
        ChessMove partialMove = new ChessMove(move.getStartPosition(), partialEndPosition, null);
        makeMove(partialMove);
        ChessMove completedMove = new ChessMove(partialEndPosition, move.getEndPosition(), null);
        makeMove(completedMove);
    }

    private void updateKingPosition(ChessPiece piece, ChessPosition position) {
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
            switch (piece.getTeamColor()) {
                case ChessGame.TeamColor.WHITE -> whiteKingLocation = position;
                case ChessGame.TeamColor.BLACK -> blackKingLocation = position;
            }
        }
    }

    private void updateCastleStatus(ChessPiece piece, ChessPosition position) {
        boolean kingMoved = piece.getPieceType() == ChessPiece.PieceType.KING;
        boolean rookMovedFromInitialSquare = (piece.getPieceType() == ChessPiece.PieceType.ROOK && isRookStartingSquare(piece, position));
        if (kingMoved) {
            switch (piece.getTeamColor()) {
                case WHITE -> {
                    whiteCastlingRights = whiteCastlingRights.setQueenSide(false);
                    whiteCastlingRights = whiteCastlingRights.setKingSide(false);
                }
                case BLACK -> {
                    blackCastlingRights = blackCastlingRights.setQueenSide(false);
                    blackCastlingRights = blackCastlingRights.setKingSide(false);
                }
            }
        }
        else if (rookMovedFromInitialSquare) {
            switch (piece.getTeamColor()) {
                case WHITE -> {
                    switch (position.getColumn()) {
                        case 1 -> whiteCastlingRights = whiteCastlingRights.setQueenSide(false);
                        case 8 -> whiteCastlingRights = whiteCastlingRights.setKingSide(false);
                    }
                }
                case BLACK -> {
                    switch (position.getColumn()) {
                        case 1 -> blackCastlingRights = blackCastlingRights.setQueenSide(false);
                        case 8 -> blackCastlingRights = blackCastlingRights.setKingSide(false);
                    }
                }
            }
        }
    }

    public boolean isRookStartingSquare(ChessPiece piece, ChessPosition position) {
        if (piece == null) {
            return false;
        }
        int row = switch (piece.getTeamColor()) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        int col1 = 1;
        int col2 = 8;
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
