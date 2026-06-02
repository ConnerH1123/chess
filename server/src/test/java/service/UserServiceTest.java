package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import request.*;

import static org.junit.jupiter.api.Assertions.fail;

public class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void testRegisterAddsUser() {
        String username = "bob";
        String password = "soup";
        String email = "jive";
        RegisterRequest r = new RegisterRequest(username, password, email);
        try {
            userService.register(r);
            UserData expected = new UserData(username,password,email);
            UserData actual = userDAO.getUser(r.username());
            Assertions.assertEquals(expected, actual);
        } catch (Exception e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void testNeedsUniqueUsername() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        RegisterRequest r = new RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        try {
            userService.register(r);
            fail("AlreadyTakenException should have been thrown");
        } catch (Exception e) {
            //Passes if no exception is thrown
        }
    }

    @Test
    public void testLogin() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        RegisterRequest r = new RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        LoginRequest login = new LoginRequest(username, password);
        try {
            userService.login(login);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
    }

    @Test
    public void testLoginInvalidPassword() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        RegisterRequest r = new RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        LoginRequest login = new LoginRequest(username, "password");
        try {
            userService.login(login);
            fail("Exception not thrown for invalid password");
        } catch (Exception e) {
            //Pass if exception is thrown
        }
    }

    @Test
    public void testLogout() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        RegisterRequest r = new RegisterRequest(username, password, email);
        RegisterResult result = new RegisterResult(username, username);
        try {
            result = userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        LogoutRequest logout = new LogoutRequest(result.authToken());
        try {
            userService.logout(logout);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
    }

    @Test
    public void testLogoutIncorrectAuthorization() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        RegisterRequest r = new RegisterRequest(username, password, email);
        RegisterResult result = new RegisterResult(username, username);
        try {
            result = userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        LogoutRequest logout = new LogoutRequest(password);
        try {
            userService.logout(logout);
            fail("Exception not thrown for invalid password");
        } catch (Exception e) {
            //Pass if exception is thrown
        }
    }
}
