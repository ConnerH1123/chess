package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String password = "bob";
        String email = "bob";
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
        String username = "bob";
        String password = "bob";
        String email = "bob";
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
}
