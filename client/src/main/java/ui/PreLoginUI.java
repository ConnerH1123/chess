package ui;

import client.ServerFacade;

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
        }
    }

    private String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "quit" -> "quit";
            case "help" -> help();
            default -> {
                System.out.print(cmd + " was not a recognized command. ");
                yield help();
            }
        };
    }

    private String help() {
        System.out.println("These are your options:");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  quit - playing chess");
        System.out.println("  help - with possible commands");
        return "help";
    }
}
