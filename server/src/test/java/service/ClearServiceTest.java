package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void testClear() {
        userDAO.createUser(new UserData("username", "password", "email"));
        authDAO.createAuth(new AuthData("authToken", "username"));
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        clearService.clear();
        Assertions.assertNull(userDAO.getUser("username"));
        Assertions.assertNull(authDAO.getAuth("authToken"));
        Assertions.assertNull(gameDAO.getGame(1));
        Assertions.assertNull(gameDAO.getGame(2));
        Assertions.assertNull(gameDAO.getGame(3));
    }
}
