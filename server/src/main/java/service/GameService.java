package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.*;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult create(CreateRequest r) throws UnauthorizedException{
        String authToken = r.authToken();
        String gameName = r.gameName();
        int gameID = gameDAO.size();
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        gameDAO.createGame(gameName);
        return new CreateResult(gameID);
    }

    public record CreateRequest(String authToken, String gameName) {};
    public record CreateResult(int gameID) {};

    public ListResult list(ListRequest r) throws UnauthorizedException {
        String authToken = r.authToken();
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        GameData[] games = gameDAO.listGames();
        return new ListResult(games);
    }

    public record ListRequest(String authToken) {}
    public record ListResult(GameData[] games) {}

    //public JoinResult join(JoinRequest r) {}
}
