package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class GameplayUI {
    private final ServerFacade server;
    private final ChessGame chessGame;

    public GameplayUI(ServerFacade server, ChessGame chessGame) {
        this.server = server;
        this.chessGame = chessGame;
    }

    public String start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Exiting...")) {
            System.out.print("[GAMEPLAY] >>> ");
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
        return result;
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
//        String[] params = (!cmd.equals("help")) ? Arrays.copyOfRange(tokens, 1, tokens.length) : null;
//        try {
            return switch (cmd) {
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> {
                    System.out.print("'" + cmd + "' was not a recognized command. ");
                    yield help();
                }
            };
//        } catch (ResponseException e) {
//            return e.getMessage();
//        }
    }

    private String help() {
        return """
                These are your options:
                  redraw - the board
                  quit - playing chess
                  help - with possible commands
                """;
    }
}
