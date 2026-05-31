package client;

import chess.*;

import java.util.Objects;

import static ui.EscapeSequences.*;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♕");
        ChessBoard defaultBoard = new ChessBoard();
        drawBoard(defaultBoard, ChessGame.TeamColor.WHITE);
    }

    public static void drawBoard(ChessBoard board, ChessGame.TeamColor color) {
        int[] indexValues = getIndexValues(color);
        int iStart = 0;
        int iEnd = 0;
        int iIncrement = 0;
        int jStart = 0;
        int jEnd = 0;
        int jIncrement = 0;

        for (int i = iStart; i != iEnd; i += iIncrement) {
            for (int j = jStart; j != jEnd; j += jIncrement) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                String piece = pieceToString(board.getPiece(currentPosition));
                System.out.print(piece);
            }
            System.out.print("\n");
        }
    }

    private static String pieceToString(ChessPiece piece) {
        String returnString = EMPTY;
        if (piece == null) {
            return returnString;
        }
        switch (piece.getTeamColor()) {
            case WHITE -> {
                returnString = switch (piece.getPieceType()) {
                    case KING -> WHITE_KING;
                    case QUEEN -> WHITE_QUEEN;
                    case ROOK -> WHITE_ROOK;
                    case BISHOP -> WHITE_BISHOP;
                    case KNIGHT -> WHITE_KNIGHT;
                    case PAWN -> WHITE_PAWN;
                };
            }
            case BLACK -> {
                returnString = switch (piece.getPieceType()) {
                    case KING -> BLACK_KING;
                    case QUEEN -> BLACK_QUEEN;
                    case ROOK -> BLACK_ROOK;
                    case BISHOP -> BLACK_BISHOP;
                    case KNIGHT -> BLACK_KNIGHT;
                    case PAWN -> BLACK_PAWN;
                };
            }
        }
        return returnString;
    }

    private static int[] getIndexValues(ChessGame.TeamColor color) {
        int[] indexValues;
        switch (color) {
            case WHITE -> {
                indexValues = new int[]{8, 0, -1, 0, 9, 1};
            }
            case BLACK -> {
                indexValues = new int[]{0,9,1,8,0,-1};
            }
            default -> throw new IllegalStateException("Unexpected value: " + color);
        }
        return indexValues;
    }
}
