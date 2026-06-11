package service;

import chess.*;
import dataaccess.*;
import model.*;
import request.*;

import java.util.Objects;

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
        validatePlayerColor(gameData, playerColor, username);
        gameDAO.updateGame(gameID, playerColor, username);
    }

    public void leave(LeaveRequest r) throws DataAccessException {
        AuthData authData = authorize(r.authToken());
        String username = authData.username();
        int gameID = r.gameID();
        String playerColor = r.playerColor();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: gameID does not exist");
        }
        validateUsername(gameData, playerColor, username);
        if (playerColor != null) {
            gameDAO.updateGame(gameID, playerColor, null);
        }
    }

    public void update(UpdateRequest r) throws DataAccessException {
        authorize(r.authToken());
        int gameID = r.gameID();
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new BadRequestException("Error: gameID does not exist");
        }
        boolean resignStatus = r.resignStatus();
        ChessMove move = r.move();
        ChessGame game = gameData.game();
        if (resignStatus) {
            game.resign();
        }
        try {
            if (move != null) {
                game.makeMove(move);
            }
        } catch (InvalidMoveException e) {
            throw new BadRequestException(e.getMessage());
        }
        gameDAO.updateGame(gameID, game);
    }

    private void validateUsername(GameData gameData, String playerColor, String username) throws DataAccessException {
        switch (playerColor) {
            case "WHITE" -> {
                if (!Objects.equals(gameData.whiteUsername(), username)) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            case "BLACK" -> {
                if (!Objects.equals(gameData.blackUsername(), username)) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            default -> throw new BadRequestException("Error: invalid player color");
        }
    }

    private void validatePlayerColor(GameData gameData, String playerColor, String username) throws DataAccessException {
        switch (playerColor) {
            case "WHITE" -> {
                if (!Objects.equals(gameData.whiteUsername(), null) && !Objects.equals(gameData.whiteUsername(), username)) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            case "BLACK" -> {
                if (!Objects.equals(gameData.blackUsername(), null) && !Objects.equals(gameData.blackUsername(), username)) {
                    throw new AlreadyTakenException("Error: user already taken");
                }
            }
            case null -> {
            }
            default -> throw new BadRequestException("Error: invalid player color");
        }
    }
}
