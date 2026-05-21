package dataaccess;

import model.GameData;

public interface GameDAO {
    abstract void createGame(String gameName);
    abstract Integer size();
    abstract GameData getGame(int gameID);
    abstract GameData[] listGames();
    abstract void updateGame(int gameID, String playerColor, String username);
    abstract void deleteAllGames();
}
