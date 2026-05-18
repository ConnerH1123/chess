package chess;

public class ValidChessPiece {
    private static final ChessGame.TeamColor WHITE = ChessGame.TeamColor.WHITE;
    private static final ChessGame.TeamColor BLACK = ChessGame.TeamColor.BLACK;

    private static final ChessPiece.PieceType KING = ChessPiece.PieceType.KING;
    private static final ChessPiece.PieceType QUEEN = ChessPiece.PieceType.QUEEN;
    private static final ChessPiece.PieceType ROOK = ChessPiece.PieceType.ROOK;
    private static final ChessPiece.PieceType BISHOP = ChessPiece.PieceType.BISHOP;
    private static final ChessPiece.PieceType KNIGHT = ChessPiece.PieceType.KNIGHT;
    private static final ChessPiece.PieceType PAWN = ChessPiece.PieceType.PAWN;

    public static final ChessPiece WHITE_KING = new ChessPiece(WHITE, KING);
    public static final ChessPiece WHITE_QUEEN = new ChessPiece(WHITE, QUEEN);
    public static final ChessPiece WHITE_ROOK = new ChessPiece(WHITE, ROOK);
    public static final ChessPiece WHITE_BISHOP = new ChessPiece(WHITE, BISHOP);
    public static final ChessPiece WHITE_KNIGHT = new ChessPiece(WHITE, KNIGHT);
    public static final ChessPiece WHITE_PAWN = new ChessPiece(WHITE, PAWN);
    public static final ChessPiece BLACK_KING = new ChessPiece(BLACK, KING);
    public static final ChessPiece BLACK_QUEEN = new ChessPiece(BLACK, QUEEN);
    public static final ChessPiece BLACK_ROOK = new ChessPiece(BLACK, ROOK);
    public static final ChessPiece BLACK_BISHOP = new ChessPiece(BLACK, BISHOP);
    public static final ChessPiece BLACK_KNIGHT = new ChessPiece(BLACK, KNIGHT);
    public static final ChessPiece BLACK_PAWN = new ChessPiece(BLACK, PAWN);
}
