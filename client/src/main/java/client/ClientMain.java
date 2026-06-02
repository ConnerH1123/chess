package client;

import chess.*;

import static ui.EscapeSequences.*;
import static ui.DrawBoard.drawBoard;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♕");
        ChessBoard defaultBoard = new ChessBoard();
        ChessGame.TeamColor team = ChessGame.TeamColor.WHITE;
        String border = SET_BG_COLOR_BLACK;
        String text = RESET_TEXT_COLOR;
        String lightSquare = SET_BG_COLOR_LIGHT_GREY;
        String darkSquare = SET_BG_COLOR_DARK_GREY;
        String whitePiece = SET_TEXT_COLOR_WHITE;
        String blackPiece = SET_TEXT_COLOR_BLACK;
        String moveColor = SET_BG_COLOR_BLUE;
        ChessMove move = new ChessMove(new ChessPosition(2,5), new ChessPosition(4,5), null);
        drawBoard(defaultBoard, team, border, text, lightSquare, darkSquare, whitePiece, blackPiece);
    }
}
