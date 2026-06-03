package ui;

import client.ResponseException;
import client.ServerFacade;
import request.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI extends UserInterface {
    private final ServerFacade server;

    public PreLoginUI(ServerFacade server) {
        this.server = server;
    }

    public void start() {
        help();
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print("[LOGGED_OUT] >>> ");
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
        }
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "register" -> register(params);
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

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            server.register(request);
            return String.format("User %s successfully registered", username);
        }
        throw new ResponseException("Insufficient arguments. Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String help() {
        return """
                These are your options:
                  register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                  login <USERNAME> <PASSWORD> - to play chess
                  quit - playing chess
                  help - with possible commands
                """;
    }
}
