package handler;

import dataaccess.*;
import service.UserService;
import service.UserService.*;
import service.GameService;
import service.GameService.*;
import service.ClearService;
import service.ClearService.*;


public class Handler {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private UserService userService = new UserService(userDAO, authDAO);
    private GameService gameService = new GameService(gameDAO, authDAO);
    private ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);


    public Handler() throws DataAccessException {
        System.out.println("DEBUG: Handler() initialization started");
        try {
            System.out.println("DEBUG: creating userDAO...");
            userDAO = new MySqlUserDAO();
            System.out.println("DEBUG: UserDAO created. Creating authDAO...");
            authDAO = new MemoryAuthDAO();
            System.out.println("DEBUG: authDAO created. Creating gameDAO...");
            gameDAO = new MemoryGameDAO();
            System.out.println("DEBUG: gameDAO created. Creating userService...");
            userService = new UserService(userDAO, authDAO);
            System.out.println("DEBUG: userService created. Creating gameService...");
            gameService = new GameService(gameDAO, authDAO);
            System.out.println("DEBUG: gameService created. Creating clearService...");
            clearService = new ClearService(userDAO, gameDAO, authDAO);
            System.out.println("DEBUG: clearService created.");
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in Handler");
            System.err.println("DEBUG: Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new DataAccessException(String.format("unable to load database: %s", e.getMessage()));
        }
    }


    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        System.out.println("DEBUG: register() called");
        return userService.register(r);
    }

    public LoginResult login(LoginRequest r) throws DataAccessException {
        System.out.println("DEBUG: login() called");
        return userService.login(r);
    }

    public void logout(LogoutRequest r) throws DataAccessException {
        System.out.println("DEBUG: logout() called");
        userService.logout(r);
    }

    public CreateResult create(CreateRequest r) throws DataAccessException {
        System.out.println("DEBUG: create() called");
        return gameService.create(r);
    }

    public ListResult list(ListRequest r) throws UnauthorizedException {
        return gameService.list(r);
    }

    public void join(JoinRequest r) throws DataAccessException {
        gameService.join(r);
    }

    public void clear() throws DataAccessException {
        System.out.println("DEBUG: clear() called");
        clearService.clear();
    }
}
