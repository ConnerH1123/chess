package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult create(CreateRequest r) throws UnauthorizedException{
        String gameName = r.gameName();
        int gameID = gameDAO.size() + 1;
        authorize(r.authToken());
        gameDAO.createGame(gameName);
        return new CreateResult(gameID);
    }

    public record CreateRequest(String authToken, String gameName) {
        public CreateRequest setAuthToken(String authToken) {
            return new CreateRequest(authToken, this.gameName);
        }
    }
    public record CreateResult(int gameID) {};

    public ListResult list(ListRequest r) throws UnauthorizedException {
        authorize(r.authToken());
        GameData[] games = gameDAO.listGames();
        return new ListResult(games);
    }

    public record ListRequest(String authToken) {}
    public record ListResult(GameData[] games) {}

    public void join(JoinRequest r) throws DataAccessException {
        String authToken = r.authToken();
        authorize(authToken);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        String playerColor = r.playerColor();
        int gameID = r.gameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: gameID does not exist");
        }
        if (playerColor.equals("WHITE") && gameData.whiteUsername() != null) {
            throw new AlreadyTakenException("Error: user already taken");
        }
        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
            throw new AlreadyTakenException("Error: user already taken");
        }
        if (!playerColor.equals("BLACK") && !playerColor.equals("WHITE")) {
            throw new BadRequestException("Error: invalid player color");
        }
        gameDAO.updateGame(gameID, playerColor, username);
    }

    public record JoinRequest(String authToken, String playerColor, Integer gameID) {
        public JoinRequest setAuthToken(String authToken) {
            return new JoinRequest(authToken, this.playerColor, this.gameID);
        }
    }

    public void authorize(String authToken) throws UnauthorizedException{
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
