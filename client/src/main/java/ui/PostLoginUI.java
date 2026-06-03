package ui;

import client.ResponseException;
import client.ServerFacade;
import request.*;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private boolean isInGame = false;

    public PostLoginUI(ServerFacade server) {
        this.server = server;
    }

    public String start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            isInGame = false;
            System.out.print("[LOGGED_IN] >>> ");
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
            if (isInGame) {
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
