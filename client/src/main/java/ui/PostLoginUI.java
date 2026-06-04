package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;
import request.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostLoginUI {
    private final ServerFacade server;
    private ChessGame chessGame = null;
    private String teamColor = "WHITE";

    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final String outputColor = RESET_TEXT_COLOR;
    private final String errorColor = SET_TEXT_COLOR_RED;
    private final String defaultColor = RESET_TEXT_COLOR;


    public PostLoginUI(ServerFacade server) {
        this.server = server;
    }

    public String start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Exiting...") && !result.equals("Logging out...")) {
            try {
                chessGame = null;
                teamColor = "WHITE";
                System.out.print(inputColor + "[LOGGED_IN] >>> " + defaultColor);
                String line = scanner.nextLine();
                result = eval(line);
                System.out.println(result);
                if (chessGame != null) {
                    GameplayUI ui = new GameplayUI(server, chessGame, teamColor);
                    result = ui.start();
                }
            } catch (Exception e) {
                System.out.println(errorColor + "System error. Error message: " + e.getMessage() + defaultColor);
            }
        }
        try {
            logout();
        } catch (ResponseException e) {
            System.out.println(errorColor + e.getMessage() + defaultColor);
        }
        return result;
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = (!cmd.equals("help")) ? Arrays.copyOfRange(tokens, 1, tokens.length) : null;
        try {
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> "Logging out...";
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> {
                    System.out.print(errorColor + "'" + cmd + "' was not a recognized command. " + defaultColor);
                    yield help();
                }
            };
        } catch (ResponseException e) {
            return errorColor + e.getMessage() + "\n" + defaultColor;
        }
    }

    private String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            CreateRequest request = new CreateRequest(null, gameName);
            server.createGame(request);
            return String.format(outputColor + "Game %s successfully created\n" + defaultColor, gameName);
        }
        throw new ResponseException("Insufficient arguments. Expected: <NAME>");
    }

    private String list() throws ResponseException {
        GameData[] games = server.listGames();
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (GameData game : games) {
            if (game == null) {
                break;
            }
            sb.append(String.format(outputColor + "%d. %s (White: %s, Black: %s)\n" + defaultColor, index, game.gameName(),
                                (game.whiteUsername() != null) ? game.whiteUsername() : "N/A",
                                (game.blackUsername() != null) ? game.blackUsername() : "N/A"));
            index++;
        }
        return sb.toString();
    }

    private String join(String... params) throws ResponseException {
        if (params.length >= 2) {
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException("Error: Invalid ID");
            }
            String team = params[1].toUpperCase();
            GameData[] games = server.listGames();
            if (gameNumber <= 0 || gameNumber > games.length) {
                throw new ResponseException("Error: Invalid ID");
            }
            GameData game = games[gameNumber-1];
            if (game == null) {
                throw new ResponseException("Error: Invalid ID");
            }
            int gameID = game.gameID();
            JoinRequest request = new JoinRequest(null, team, gameID);
            server.joinGame(request);
            chessGame = game.game();
            teamColor = team;
            return String.format(outputColor + "Game %s successfully joined as %s\n" + defaultColor, game.gameName(), team);
        }
        throw new ResponseException("Insufficient arguments. Expected: <ID> <WHITE|BLACK>");
    }

    private String observe(String... params) throws ResponseException {
        if (params.length >= 1) {
            int gameNumber;
            try {
                gameNumber = Integer.parseInt(params[0]);
            } catch (Exception e) {
                throw new ResponseException("Error: Invalid ID");
            }
            GameData[] games = server.listGames();
            if (gameNumber <= 0 || gameNumber > games.length) {
                throw new ResponseException("Error: Invalid ID");
            }
            GameData game = games[gameNumber-1];
            if (game == null) {
                throw new ResponseException("Error: Invalid ID");
            }
            chessGame = game.game();
            return String.format(outputColor + "Game %s successfully joined as observer\n" + defaultColor, game.gameName());
        }
        throw new ResponseException("Insufficient arguments. Expected: <ID>");
    }

    private void logout() throws ResponseException {
        server.logout();
    }

    private String help() {
        return outputColor + """
                These are your options:
                  create <NAME> - a game
                  list - games
                  join <ID> <WHITE|BLACK> - a game
                  observe <ID> - a game
                  logout - when you are done
                  quit - playing chess
                  help - with possible commands
                """ + defaultColor;
    }
}
