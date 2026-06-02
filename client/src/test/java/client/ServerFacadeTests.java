package client;

import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LoginResult;
import request.RegisterRequest;
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

}
