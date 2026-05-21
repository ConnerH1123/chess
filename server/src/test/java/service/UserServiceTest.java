package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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
        UserService.RegisterRequest r = new UserService.RegisterRequest(username, password, email);
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
        UserService.RegisterRequest r = new UserService.RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        try {
            userService.register(r);
            fail("AlreadyTakenException should have been thrown");
        } catch (AlreadyTakenException e) {
            //Passes if no exception is thrown
        }
    }

    @Test
    public void testLogin() {
        String username = "joe";
        String password = "joe mama";
        String email = "xD";
        UserService.RegisterRequest r = new UserService.RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        UserService.LoginRequest login = new UserService.LoginRequest(username, password);
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
        UserService.RegisterRequest r = new UserService.RegisterRequest(username, password, email);
        try {
            userService.register(r);
        } catch (Exception e) {
            fail("Exception thrown during initial register");
        }
        UserService.LoginRequest login = new UserService.LoginRequest(username, "password");
        try {
            userService.login(login);
            fail("Exception not thrown for invalid password");
        } catch (UnauthorizedException e) {
            //Pass if exception is thrown
        }
    }
}
