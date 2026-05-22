package dataaccess;

import model.GameData;

public interface GameDAO {
    /**
     * Creates new game data and adds it
     *
     * @param gameName Name of game
     */
    void createGame(String gameName);

    /**
     * Returns size of database
     *
     * @return Number of games in database
     */
    Integer size();

    /**
     * Given gameID retrieves game
     *
     * @param gameID ID of game (int greater than 0)
     * @return Game data or null
     */
    GameData getGame(int gameID);

    /**
     * Lists games in database
     *
     * @return Array of game data
     */
    GameData[] listGames();

    /**
     * Adds user to game
     *
     * @param gameID ID of game
     * @param playerColor Side that player wishes to play as
     * @param username Name of user
     */
    void updateGame(int gameID, String playerColor, String username);

    /**
     * Clears database
     */
    void deleteAllGames();
}
