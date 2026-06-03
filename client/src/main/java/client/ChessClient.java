package client;

import ui.PreLoginUI;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to the chess server ♕");
        PreLoginUI ui = new PreLoginUI(server);
        ui.start();
    }

}
