package ui;

import client.ServerFacade;

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

        }
    }

    private void help() {
        System.out.println("These are your options:");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  quit - playing chess");
        System.out.println("  help - with possible commands");
    }
}
