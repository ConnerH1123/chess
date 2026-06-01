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
        int iStart = indexValues[0];
        int iEnd = indexValues[1];
        int iIncrement = indexValues[2];
        int jStart = indexValues[3];
        int jEnd = indexValues[4];
        int jIncrement = indexValues[5];
        printColumns(jStart, jEnd, jIncrement, SET_BG_COLOR_BLACK, RESET_TEXT_COLOR);
        for (int i = iStart; i != iEnd; i += iIncrement) {
            System.out.print(SET_BG_COLOR_BLACK + " " + i + " " + RESET_BG_COLOR);
            for (int j = jStart; j != jEnd; j += jIncrement) {
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece pieceLiteral = board.getPiece(currentPosition);
                String piece = pieceToString(pieceLiteral);
                setSquareColor(SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREY, i, j);
                setPieceColor(pieceLiteral, SET_TEXT_COLOR_WHITE, SET_TEXT_COLOR_BLACK);
                System.out.print(piece + RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            System.out.println(SET_BG_COLOR_BLACK + " " + i + " " + RESET_BG_COLOR);
        }
        printColumns(jStart, jEnd, jIncrement, SET_BG_COLOR_BLACK, RESET_TEXT_COLOR);
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
                indexValues = new int[]{8, 0, -1, 1, 9, 1};
            }
            case BLACK -> {
                indexValues = new int[]{1,9,1,8,0,-1};
            }
            default -> throw new IllegalStateException("Unexpected value: " + color);
        }
        return indexValues;
    }

    private static void printColumns(int start, int end, int increment, String background, String text) {
        System.out.print(background + text);
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H"};
        System.out.print("\u2003" + "\u2003" + " ");
        for (int i = start; i != end; i += increment) {
            System.out.print(columns[i-1] + "\u2003" + " ");
        }
        System.out.println("\u2003" + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private static void setSquareColor(String white, String black, int row, int col) {
        if ((row+col) % 2 == 0) {
            System.out.print(black);
        }
        else {
            System.out.print(white);
        }
    }

    private static void setPieceColor(ChessPiece piece, String white, String black) {
        if (piece == null) {
            return;
        }
        switch (piece.getTeamColor()) {
            case WHITE -> System.out.print(white);
            case BLACK -> System.out.print(black);
        }
    }

}
