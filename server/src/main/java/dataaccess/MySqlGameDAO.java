package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlGameDAO extends SqlDatabase implements GameDAO {
    private final String tableName;
    private int size;

    private int getSize() throws DataAccessException {
        String statement = "SELECT COUNT(*) AS rowCount FROM " + tableName;
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("rowCount");
                    }
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
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
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM " + tableName + " WHERE gameID=?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1,gameID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameName = resultSet.getString("gameName");
                        String json = resultSet.getString("json");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM " + tableName;
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    GameData[] games = new GameData[size];
                    int i = 0;
                    while (resultSet.next()) {
                        int gameID = resultSet.getInt("gameID");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameName = resultSet.getString("gameName");
                        String json = resultSet.getString("json");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        games[i] = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
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
        updateDatabase(statement, gameID, json);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE " + tableName;
        updateDatabase(statement);
    }
}
