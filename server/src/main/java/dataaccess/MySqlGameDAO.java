package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

public class MySqlGameDAO extends SqlDatabase implements GameDAO {
    private final String tableName;
    private int size = 0;

    public MySqlGameDAO() throws DataAccessException {
        tableName = "game";
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    public MySqlGameDAO(String tableName) throws DataAccessException {
        this.tableName = tableName;
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    private String[] loadStatements() {
        return new String[]{"CREATE TABLE IF NOT EXISTS  " + tableName + " (\n" +
                "`gameId` int NOT NULL AUTO_INCREMENT,\n" +
                "`whiteUsername` varchar(256) DEFAULT NULL,\n" +
                "`blackUsername` varchar(256) DEFAULT NULL,\n" +
                "`gameName` varchar(256) NOT NULL,\n" +
                "`json` TEXT DEFAULT NULL,\n" +
                "PRIMARY KEY (`gameId`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
        };
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO " + tableName + " (gameName, json) VALUES (?, ?)";
        String json = new Gson().toJson(new ChessGame());
        size = updateDatabase(statement, gameName, json);
    }

    @Override
    public Integer size() {
        return size;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public GameData[] listGames() {
        return new GameData[0];
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {

    }

    @Override
    public void updateGame(int gameID, ChessGame updatedGame) {

    }

    @Override
    public void deleteAllGames() {

    }
}
