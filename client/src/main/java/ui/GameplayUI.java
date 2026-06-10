package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;
import request.*;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

public class GameplayUI implements ServerMessageHandler {
    private WebSocketFacade ws = null;
    private final GameData gamedata;
    private final ChessGame chessGame;
    private final String teamColor;
    private final String username;

    private final String border = SET_BG_COLOR_BLACK;
    private final String text = RESET_TEXT_COLOR;
    private final String lightSquares = SET_BG_COLOR_LIGHT_GREY;
    private final String darkSquares = SET_BG_COLOR_DARK_GREY;
    private final String whitePieces = SET_TEXT_COLOR_WHITE;
    private final String blackPieces = SET_TEXT_COLOR_BLACK;

    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final String outputColor = RESET_TEXT_COLOR;
    private final String errorColor = SET_TEXT_COLOR_RED;
    private final String messageColor = SET_TEXT_COLOR_YELLOW;
    private final String defaultColor = RESET_TEXT_COLOR;

    public GameplayUI(String serverURL, GameData gameData, String teamColor) {
        this.gamedata = gameData;
        this.chessGame = gameData.game();
        this.teamColor = teamColor;
        username = switch (teamColor) {
            case "WHITE" -> gameData.whiteUsername();
            case "BLACK" -> gameData.blackUsername();
            default -> null;
        };
        try {
            this.ws = new WebSocketFacade(serverURL, this);
            ws.connect(gameData.gameID(), username);
        } catch (ResponseException e) {
            System.out.println(errorColor + "Unable to connect with websocket: " + e + defaultColor);
        }
    }

    String prompt = "[GAMEPLAY] >>> ";
    public String start(String authToken) {
        System.out.println(redraw());
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Exiting...") && !result.equals("Leaving...") && (ws != null)) {
            System.out.print(inputColor + prompt + defaultColor);
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
        return result;
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        try {
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
        } catch (ResponseException e) {
            return errorColor + e.getMessage() + "\n" + defaultColor;
        }
    }

    private String redraw() {
        ChessGame.TeamColor color;
        if (teamColor == null) {
            color = WHITE;
        }
        else if (teamColor.equals("BLACK")) {
            color = BLACK;
        }
        else {
            color = WHITE;
        }
        drawBoard(chessGame.getBoard(), color, border, text, lightSquares, darkSquares, whitePieces, blackPieces);
        return String.format("%s to move\n", chessGame.getTeamTurn().toString());
    }

    private String leave() throws ResponseException {
        ws.leave(gamedata.gameID(), username);
        return "Leaving...";
    }

    private String help() {
        return outputColor + """
                These are your options:
                  redraw - the board
                  leave - the game
                  quit - playing chess
                  help - with possible commands
                """ + defaultColor;
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        System.out.print("\033[2K\r");
        System.out.flush();
        System.out.println(messageColor + serverMessage.getMessage() + defaultColor);
        System.out.print(inputColor + prompt + defaultColor);
    }
}
