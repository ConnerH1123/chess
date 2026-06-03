package client;

import ui.PreLoginUI;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess Server ♕");
        PreLoginUI ui = new PreLoginUI(server);
        ui.start();
        System.out.println("Exiting chess server. Come again soon!");
    }

}
