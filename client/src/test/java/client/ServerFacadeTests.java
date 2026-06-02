package client;

import org.junit.jupiter.api.*;
import request.*;
import server.Server;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegister() {
        String randomStr = UUID.randomUUID().toString();
        RegisterRequest registerRequest = new RegisterRequest(randomStr, "password", "email");
        try {
            facade.register(registerRequest);
        } catch (ResponseException e) {
            Assertions.fail("Error message thrown: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterExistingUser() {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        try {
            facade.register(registerRequest);
            facade.register(registerRequest);
            Assertions.fail("Exception not thrown for repeated username");
        } catch (ResponseException e) {
            //
        }
    }

    @Test
    public void testLogin() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(username, password);
            facade.login(loginRequest);
        } catch (ResponseException e) {
            Assertions.fail("Error message thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoginIncorrectPassword() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(username, "incorrect password");
            facade.login(loginRequest);
            Assertions.fail("Exception not thrown for incorrect password");
        } catch (ResponseException e) {
            //
        }
    }

    @Test
    public void testLogout() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            facade.logout();
        } catch (ResponseException e) {
            Assertions.fail("Error message thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLogoutBeforeLogin() {
        try {
            facade.logout();
            Assertions.fail("Shouldn't be able to logout before logging in");
        } catch (ResponseException e) {
            //
        }
    }

    @Test
    public void testCreateGame() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            String gameName = UUID.randomUUID().toString();
            CreateRequest createRequest = new CreateRequest(null, gameName);
            facade.createGame(createRequest);
        } catch (ResponseException e) {
            Assertions.fail("Error message thrown: " + e.getMessage());
        }
    }

    @Test
    public void testCreateGameNullName() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            CreateRequest createRequest = new CreateRequest(null, null);
            facade.createGame(createRequest);
            Assertions.fail("No error message thrown for null game name");
        } catch (ResponseException e) {
            //
        }
    }

    @Test
    public void testListGames() {
        String username = UUID.randomUUID().toString();
        String password = "password";
        String email = "email";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            facade.register(registerRequest);
            String gameName = UUID.randomUUID().toString();
            CreateRequest createRequest = new CreateRequest(null, gameName);
            facade.createGame(createRequest);
            facade.listGames();
        } catch (ResponseException e) {
            Assertions.fail("Error message thrown: " + e.getMessage());
        }
    }

    @Test
    public void testListGamesBeforeLogin() {
        try {
            facade.listGames();
            Assertions.fail("Shouldn't be able to list games before logging in");
        } catch (ResponseException e) {
            //
        }
    }

}
