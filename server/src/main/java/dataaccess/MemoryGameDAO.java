package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> gameDatabase = new HashMap<>();

    @Override
    public void createGame(String gameName) {
        int gameID = gameDatabase.size();
        GameData gameData = new GameData(gameID, "","", gameName, new ChessGame());
        gameDatabase.put(gameID, gameData);
    }

    @Override
    public int size() {
        return gameDatabase.size();
    }

    @Override
    public GameData getGame(int gameID) {
        return gameDatabase.get(gameID);
    }

    @Override
    public GameData[] listGames() {
        GameData[] games = new GameData[gameDatabase.size()];
        int i = 0;
        for (GameData game : gameDatabase.values()) {
            games[i] = game;
            i++;
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {
        GameData game = gameDatabase.get(gameID);
        GameData newGameData;
        if (playerColor.equals("WHITE")) {
            newGameData = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else if (playerColor.equals("BLACK")) {
            newGameData = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        else {
            newGameData = game;
        }
        gameDatabase.put(gameID, newGameData);
    }

    @Override
    public void deleteGame(int gameID) {
        gameDatabase.remove(gameID);
    }

    @Override
    public void deleteAllGames() {
        gameDatabase.clear();
    }
}
