package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;
import request.*;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private ChessGame chessGame = null;

    public PostLoginUI(ServerFacade server) {
        this.server = server;
    }

    public String start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            chessGame = null;
            System.out.print("[LOGGED_IN] >>> ");
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
            if (chessGame != null) {
//                PostLoginUI ui = new PostLoginUI(server);
//                ui.start();
            }
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
                case "quit" -> "quit";
                case "help" -> help();
                default -> {
                    System.out.print("'" + cmd + "' was not a recognized command. ");
                    yield help();
                }
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            String gameName = params[0];
            CreateRequest request = new CreateRequest(null, gameName);
            server.createGame(request);
            return String.format("Game %s successfully created", gameName);
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
            sb.append(String.format("%d. %s (White: %s, Black: %s)\n", index, game.gameName(),
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
            int gameID = game.gameID();
            JoinRequest request = new JoinRequest(null, team, gameID);
            server.joinGame(request);
            chessGame = game.game();
            return String.format("Game %s successfully joined as %s", game.gameName(), team);
        }
        throw new ResponseException("Insufficient arguments. Expected: <ID> <WHITE|BLACK>");
    }

    private String help() {
        return """
                These are your options:
                  create <NAME> - a game
                  list - games
                  join <ID> <WHITE|BLACK> - a game
                  observe <ID> - a game
                  logout - when you are done
                  quit - playing chess
                  help - with possible commands
                """;
    }
}
