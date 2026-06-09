package handler;

import dataaccess.*;
import service.UserService;
import service.UserService.*;
import service.GameService;
import service.GameService.*;
import service.ClearService;
import service.ClearService.*;
import request.*;


public class Handler {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Handler() throws DataAccessException {
        try {
            UserDAO userDAO = new MySqlUserDAO();
            AuthDAO authDAO = new MySqlAuthDAO();
            GameDAO gameDAO = new MySqlGameDAO();
            userService = new UserService(userDAO, authDAO);
            gameService = new GameService(gameDAO, authDAO);
            clearService = new ClearService(userDAO, gameDAO, authDAO);
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to load database: %s", e.getMessage()));
        }
    }

    public GameService getGameService() {
        return gameService;
    }


    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        return userService.register(r);
    }

    public LoginResult login(LoginRequest r) throws DataAccessException {
        return userService.login(r);
    }

    public void logout(LogoutRequest r) throws DataAccessException {
        userService.logout(r);
    }

    public CreateResult create(CreateRequest r) throws DataAccessException {
        return gameService.create(r);
    }

    public ListResult list(ListRequest r) throws DataAccessException {
        return gameService.list(r);
    }

    public void join(JoinRequest r) throws DataAccessException {
        gameService.join(r);
    }

    public void clear() throws DataAccessException {
        clearService.clear();
    }
}
