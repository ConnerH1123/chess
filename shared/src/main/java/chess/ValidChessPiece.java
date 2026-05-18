package chess;

public class ValidChessPiece {
    private static final ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
    private static final ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

    private static final ChessPiece.PieceType king = ChessPiece.PieceType.KING;
    private static final ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;
    private static final ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
    private static final ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
    private static final ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
    private static final ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;

    public static final ChessPiece wKing = new ChessPiece(white, king);
    public static final ChessPiece wQueen = new ChessPiece(white, queen);
    public static final ChessPiece wRook = new ChessPiece(white, rook);
    public static final ChessPiece wBishop = new ChessPiece(white, bishop);
    public static final ChessPiece wKnight = new ChessPiece(white, knight);
    public static final ChessPiece wPawn = new ChessPiece(white, pawn);
    public static final ChessPiece bKing = new ChessPiece(black, king);
    public static final ChessPiece bQueen = new ChessPiece(black, queen);
    public static final ChessPiece bRook = new ChessPiece(black, rook);
    public static final ChessPiece bBishop = new ChessPiece(black, bishop);
    public static final ChessPiece bKnight = new ChessPiece(black, knight);
    public static final ChessPiece bPawn = new ChessPiece(black, pawn);
}
