package handler;

import dataaccess.*;
import service.UserService;
import service.UserService.*;
import service.GameService;
import service.GameService.*;
import service.ClearService;
import service.ClearService.*;


public class Handler {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

    public RegisterResult register(RegisterRequest r) throws AlreadyTakenException {
        return userService.register(r);
    }

    public LoginResult login(LoginRequest r) throws UnauthorizedException {
        return userService.login(r);
    }

    public void logout(LogoutRequest r) throws UnauthorizedException {
        userService.logout(r);
    }

    public CreateResult create(CreateRequest r) throws DataAccessException {
        return gameService.create(r);
    }

    public ListResult list(ListRequest r) throws UnauthorizedException {
        return gameService.list(r);
    }

    public void join(JoinRequest r) throws DataAccessException {
        gameService.join(r);
    }

    public void clear() {
        clearService.clear();
    }
}
