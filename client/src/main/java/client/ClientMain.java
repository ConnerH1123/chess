package client;

import chess.*;

import java.util.Objects;

import static ui.EscapeSequences.*;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♕");
        ChessBoard defaultBoard = new ChessBoard();
        final int PADDING = 1;
        drawBoard(defaultBoard, PADDING);
    }

    public static void drawBoard(ChessBoard board, int padding) {
        for (int i = 1; i < 9; i++) {
            System.out.print(padString("\n",padding));
            for (int j = 1; j < 9; j++) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                String piece = pieceToString(board.getPiece(currentPosition));
                System.out.print(padString(piece,padding));
            }
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

    private static String padString(String str, int padding) {
        StringBuilder newString = new StringBuilder();
        if (Objects.equals(str, "\n")) {
            for (int i = 0; i < padding; i++) {
                newString.append("\n");
            }
        }
        else {
            newString.append(str);
            for (int i = 0; i < padding; i++) {
                newString.insert(0," ");
                newString.append(" ");
            }
        }
        return newString.toString();
    }
}
