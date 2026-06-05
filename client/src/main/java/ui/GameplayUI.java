package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

public class GameplayUI {
    private final ServerFacade server;
    private final GameData gamedata;
    private final ChessGame chessGame;
    private final String teamColor;

    private final String border = SET_BG_COLOR_BLACK;
    private final String text = RESET_TEXT_COLOR;
    private final String lightSquares = SET_BG_COLOR_LIGHT_GREY;
    private final String darkSquares = SET_BG_COLOR_DARK_GREY;
    private final String whitePieces = SET_TEXT_COLOR_WHITE;
    private final String blackPieces = SET_TEXT_COLOR_BLACK;

    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final String outputColor = RESET_TEXT_COLOR;
    private final String errorColor = SET_TEXT_COLOR_RED;
    private final String defaultColor = RESET_TEXT_COLOR;

    public GameplayUI(ServerFacade server, GameData gameData, String teamColor) {
        this.server = server;
        this.gamedata = gameData;
        this.chessGame = gameData.game();
        this.teamColor = teamColor;
    }

    public String start() {
        System.out.println(redraw());
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Exiting...") && !result.equals("Leaving...")) {
            System.out.print(inputColor + "[GAMEPLAY] >>> " + defaultColor);
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
        return result;
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        return switch (cmd) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "quit" -> "Exiting...";
            case "help" -> help();
            default -> {
                System.out.print("'" + cmd + "' was not a recognized command. ");
                yield help();
            }
        };
    }

    private String redraw() {
        ChessGame.TeamColor color = switch (teamColor) {
            case "BLACK" -> BLACK;
            default -> WHITE;
        };
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces);
        return String.format("%s to move\n", chessGame.getTeamTurn().toString());
    }

    private String leave() {
        return "Leaving...";
    }

    private String help() {
        return """
                These are your options:
                  redraw - the board
                  leave - the game
                  quit - playing chess
                  help - with possible commands
                """;
    }
}
