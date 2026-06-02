package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;

public class GameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private String authToken;

    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        authToken = "token";
        AuthData authData = new AuthData("token", "username");
        authDAO.createAuth(authData);
    }

    @Test
    public void testCreate() {
        String gameName = "not checkers xD";
        CreateRequest request = new CreateRequest(authToken, gameName);
        try {
            gameService.create(request);
            GameData expected = new GameData(1,null,null,gameName, new ChessGame());
            GameData actual = gameDAO.getGame(1);
            Assertions.assertEquals(expected, actual);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
    }

    @Test
    public void testCreateNoName() {
        String gameName = null;
        CreateRequest request = new CreateRequest(authToken, gameName);
        try {
            gameService.create(request);
            Assertions.fail("Exception not thrown");
        } catch (DataAccessException e) {
            //
        }
    }

    @Test
    public void testList() {
        String gameName = "not checkers xD";
        CreateRequest request = new CreateRequest(authToken, gameName);
        String gameName1 = "maybe checkers?";
        CreateRequest request1 = new CreateRequest(authToken, gameName1);
        try {
            gameService.create(request);
            gameService.create(request1);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
        ListRequest lRequest = new ListRequest(authToken);
        try {
            gameService.list(lRequest);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
    }

    @Test
    public void testListUnauthorized() {
        String gameName = "not checkers xD";
        CreateRequest request = new CreateRequest(authToken, gameName);
        String gameName1 = "maybe checkers?";
        CreateRequest request1 = new CreateRequest(authToken, gameName1);
        try {
            gameService.create(request);
            gameService.create(request1);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
        ListRequest lRequest = new ListRequest("authToken");
        try {
            gameService.list(lRequest);
            Assertions.fail("Exception was not thrown");
        } catch (Exception e) {
            //Passes
        }
    }

    @Test
    public void testJoin() {
        String gameName = "not checkers xD";
        CreateRequest r = new CreateRequest(authToken, gameName);
        try {
            gameService.create(r);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
        JoinRequest request = new JoinRequest(authToken, "WHITE", 1);
        try {
            gameService.join(request);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
    }

    @Test
    public void testJoinInvalidColor() {
        String gameName = "not checkers xD";
        CreateRequest r = new CreateRequest(authToken, gameName);
        try {
            gameService.create(r);
        } catch (Exception e) {
            Assertions.fail("Exception was thrown");
        }
        JoinRequest request = new JoinRequest(authToken, "YELLOW-ISH GREEN", 1);
        try {
            gameService.join(request);
            Assertions.fail("Exception was not thrown");
        } catch (Exception e) {
            //
        }
    }
}
