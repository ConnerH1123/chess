package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;

public class MySqlGameDAO extends SqlDatabase implements GameDAO {
    private final String tableName;
    private int size;

    private int getSize() throws DataAccessException {
        return getTableSize(tableName);
    }

    public MySqlGameDAO() throws DataAccessException {
        tableName = "game";
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
        size = getSize();
    }

    public MySqlGameDAO(String tableName) throws DataAccessException {
        this.tableName = tableName;
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
        size = getSize();
    }

    private String[] loadStatements() {
        return new String[]{"CREATE TABLE IF NOT EXISTS  " + tableName + " (\n" +
                "`gameID` int NOT NULL AUTO_INCREMENT,\n" +
                "`whiteUsername` varchar(256) DEFAULT NULL,\n" +
                "`blackUsername` varchar(256) DEFAULT NULL,\n" +
                "`gameName` varchar(256) NOT NULL,\n" +
                "`json` TEXT DEFAULT NULL,\n" +
                "PRIMARY KEY (`gameID`)\n" +
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
    public GameData getGame(int gameID) throws DataAccessException {
        if (gameID < 0 || gameID > size) {
            throw new DataAccessException("Error: gameID outside of bounds");
        }
        String statement = "SELECT whiteUsername, blackUsername, gameName, json FROM " + tableName + " WHERE gameID=?";
        ArrayList<String[]> queryResponse = queryDatabase(statement, 4, gameID);
        if (queryResponse.isEmpty()) {
            return null;
        }
        String whiteUsername = queryResponse.getFirst()[0];
        String blackUsername = queryResponse.getFirst()[1];
        String gameName = queryResponse.getFirst()[2];
        String json = queryResponse.getFirst()[3];
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM " + tableName;
        ArrayList<String[]> queryResponse = queryDatabase(statement, 5);
        GameData[] games = new GameData[size];
        int i = 0;
        for (String[] row : queryResponse) {
            int gameID = Integer.parseInt(row[0]);
            String whiteUsername = row[1];
            String blackUsername = row[2];
            String gameName = row[3];
            String json = row[4];
            ChessGame game = new Gson().fromJson(json, ChessGame.class);
            games[i] = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            i++;
        }
        return games;
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) throws DataAccessException {
        if (gameID < 0 || gameID > size) {
            throw new DataAccessException("Error: gameID outside of bounds");
        }
        String statement = switch (playerColor) {
            case "WHITE" -> "UPDATE " + tableName + " SET whiteUsername=? WHERE gameID=?";
            case "BLACK" -> "UPDATE " + tableName + " SET blackUsername=? WHERE gameID=?";
            default -> throw new DataAccessException("Error: invalid color");
        };
        updateDatabase(statement, username, gameID);
    }

    @Override
    public void updateGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        if (gameID < 0 || gameID > size) {
            throw new DataAccessException("Error: gameID outside of bounds");
        }
        String statement = "UPDATE " + tableName + " SET json=? WHERE gameID=?";
        String json = new Gson().toJson(updatedGame);
        updateDatabase(statement, json, gameID);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE " + tableName;
        updateDatabase(statement);
        size = 0;
    }
}
