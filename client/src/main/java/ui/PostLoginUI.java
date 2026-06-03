package ui;

import client.ServerFacade;

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
            //result = eval(line);
            System.out.println(result);
            if (isInGame) {
//                PostLoginUI ui = new PostLoginUI(server);
//                ui.start();
            }
        }
        return result;
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
