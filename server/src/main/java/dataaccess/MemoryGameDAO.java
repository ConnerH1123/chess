package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> gameDatabase = new HashMap<>();

    @Override
    public void createGame(String gameName) {
        int gameID = gameDatabase.size() + 1;
        GameData gameData = new GameData(gameID, null,null, gameName, new ChessGame());
        gameDatabase.put(gameID, gameData);
    }

    @Override
    public Integer size() {
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
        GameData currentGameData = gameDatabase.get(gameID);
        GameData newGameData = switch (playerColor) {
            case "WHITE" -> new GameData(currentGameData.gameID(), username, currentGameData.blackUsername(), currentGameData.gameName(), currentGameData.game());
            case "BLACK" -> new GameData(currentGameData.gameID(), currentGameData.whiteUsername(), username, currentGameData.gameName(), currentGameData.game());
            default -> currentGameData;
        };
        gameDatabase.put(gameID, newGameData);
    }

    @Override
    public void updateGame(int gameID, ChessGame updatedGame) {
        GameData currentGameData = gameDatabase.get(gameID);
        GameData newGameData = new GameData(currentGameData.gameID(), currentGameData.whiteUsername(), currentGameData.blackUsername(), currentGameData.gameName(), updatedGame);
        gameDatabase.put(gameID, newGameData);
    }

    @Override
    public void deleteAllGames() {
        gameDatabase.clear();
    }
}
