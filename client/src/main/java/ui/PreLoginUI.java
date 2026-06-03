package ui;

import client.ResponseException;
import client.ServerFacade;
import request.*;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade server;
    private boolean isLoggedIn = false;

    public PreLoginUI(ServerFacade server) {
        this.server = server;
    }

    public void start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = " ";
        while (!result.equals("Exiting chess server...") && !result.equals("Exiting...")) {
            isLoggedIn = false;
            System.out.print("[LOGGED_OUT] >>> ");
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
            if (isLoggedIn) {
                PostLoginUI ui = new PostLoginUI(server);
                result = ui.start();
                System.out.println("Successfully logged out");
            }
        }
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = (!cmd.equals("help")) ? Arrays.copyOfRange(tokens, 1, tokens.length) : null;
        try {
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "Exiting chess server...";
                case "help" -> help();
                default -> {
                    System.out.print("'" + cmd + "' was not a recognized command. ");
                    yield help();
                }
            };
        } catch (ResponseException e) {
            return e.getMessage() + "\n";
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            server.register(request);
            isLoggedIn = true;
            return String.format("User %s successfully registered", username);
        }
        throw new ResponseException("Insufficient arguments. Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            server.login(request);
            isLoggedIn = true;
            return String.format("User %s successfully logged in", username);
        }
        throw new ResponseException("Insufficient arguments. Expected: <USERNAME> <PASSWORD>");
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
