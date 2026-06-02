package service;

import dataaccess.*;
import model.*;
import request.*;

public class GameService extends Authorizable {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public CreateResult create(CreateRequest r) throws DataAccessException {
        authorize(r.authToken());
        String gameName = r.gameName();
        gameDAO.createGame(gameName);
        int gameID = gameDAO.size();
        return new CreateResult(gameID);
    }

    public ListResult list(ListRequest r) throws DataAccessException {
        authorize(r.authToken());
        GameData[] games = gameDAO.listGames();
        return new ListResult(games);
    }

    public void join(JoinRequest r) throws DataAccessException {
        AuthData authData = authorize(r.authToken());
        String username = authData.username();
        String playerColor = r.playerColor();
        int gameID = r.gameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: gameID does not exist");
        }
        validatePlayerColor(gameData, playerColor);
        gameDAO.updateGame(gameID, playerColor, username);
    }

    private void validatePlayerColor(GameData gameData, String playerColor) throws DataAccessException {
        switch (playerColor) {
            case "WHITE" -> {
                if (gameData.whiteUsername() != null) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            case "BLACK" -> {
                if (gameData.blackUsername() != null) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            default -> throw new BadRequestException("Error: invalid player color");
        }
    }
}
