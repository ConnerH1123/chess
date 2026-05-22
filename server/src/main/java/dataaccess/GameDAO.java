package dataaccess;

import model.GameData;

public interface GameDAO {
    /**
     * Creates new game data and adds it
     *
     * @param gameName Name of game
     */
    abstract void createGame(String gameName);

    /**
     * Returns size of database
     *
     * @return Number of games in database
     */
    abstract Integer size();

    /**
     * Given gameID retrieves game
     *
     * @param gameID ID of game (int greater than 0)
     * @return Game data or null
     */
    abstract GameData getGame(int gameID);

    /**
     * Lists games in database
     *
     * @return Array of game data
     */
    abstract GameData[] listGames();

    /**
     * Adds user to game
     *
     * @param gameID ID of game
     * @param playerColor Side that player wishes to play as
     * @param username Name of user
     */
    abstract void updateGame(int gameID, String playerColor, String username);

    /**
     * Clears database
     */
    abstract void deleteAllGames();
}
