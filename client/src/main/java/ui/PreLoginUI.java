package ui;

import client.ResponseException;
import client.ServerFacade;
import request.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    private final ServerFacade server;
    private boolean isLoggedIn = false;
    private String authToken = null;
    private String username = null;

    private final String inputColor = SET_TEXT_COLOR_BLUE;
    private final String outputColor = RESET_TEXT_COLOR;
    private final String errorColor = SET_TEXT_COLOR_RED;
    private final String defaultColor = RESET_TEXT_COLOR;

    public PreLoginUI(ServerFacade server) {
        this.server = server;
    }

    public void start() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        String result = " ";
        while (!result.equals("Exiting chess server...") && !result.equals("Exiting...")) {
            try {
                authToken = null;
                isLoggedIn = false;
                System.out.print(inputColor + "[LOGGED_OUT] >>> " + defaultColor);
                String line = scanner.nextLine();
                result = eval(line);
                System.out.println(result);
                if (isLoggedIn) {
                    PostLoginUI ui = new PostLoginUI(server);
                    result = ui.start(authToken, username);
                    System.out.println(outputColor + "Successfully logged out" + defaultColor);
                }
            } catch (Exception e) {
                System.out.println(errorColor + "System error. Error message: " + e.getMessage() + defaultColor);
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
                    System.out.print(errorColor + "'" + cmd + "' was not a recognized command. " + defaultColor);
                    yield help();
                }
            };
        } catch (ResponseException e) {
            return errorColor + e.getMessage() + "\n";
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            this.username = username;
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            authToken = server.register(request);
            isLoggedIn = true;
            return String.format(outputColor + "User %s successfully registered" + defaultColor, username);
        }
        throw new ResponseException(errorColor + "Insufficient arguments. Expected: <USERNAME> <PASSWORD> <EMAIL>" + defaultColor);
    }

    private String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            this.username = username;
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            authToken = server.login(request);
            isLoggedIn = true;
            return String.format(outputColor + "User %s successfully logged in" + defaultColor, username);
        }
        throw new ResponseException(errorColor + "Insufficient arguments. Expected: <USERNAME> <PASSWORD>" + defaultColor);
    }

    private String help() {
        return outputColor + """
                These are your options:
                  register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                  login <USERNAME> <PASSWORD> - to play chess
                  quit - playing chess
                  help - with possible commands
                """ + defaultColor;
    }
}
