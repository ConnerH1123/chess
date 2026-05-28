package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    /**
     * Creates new game data and adds it
     *
     * @param gameName Name of game
     */
    void createGame(String gameName) throws DataAccessException;

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
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * Lists games in database
     *
     * @return Array of game data
     */
    GameData[] listGames() throws DataAccessException;

    /**
     * Adds user to game
     *
     * @param gameID ID of game to be updated
     * @param playerColor Side that player wishes to play as
     * @param username Name of user
     */
    void updateGame(int gameID, String playerColor, String username);


    /**
     * Updates the board
     *
     * @param gameID ID of game to be updated
     * @param updatedGame Updated board to overwrite current board
     */
    void updateGame(int gameID, ChessGame updatedGame);

    /**
     * Clears database
     */
    void deleteAllGames() throws DataAccessException;
}
