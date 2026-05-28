package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO extends SqlDatabase implements AuthDAO {
    private final String tableName;

    public MySqlAuthDAO() throws DataAccessException {
        tableName = "auth";
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    public MySqlAuthDAO(String tableName) throws DataAccessException {
        this.tableName = tableName;
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    private String[] loadStatements() {
        return new String[]{"CREATE TABLE IF NOT EXISTS  " + tableName + " (\n" +
                "`authToken` varchar(256) NOT NULL,\n" +
                "`username` varchar(256) NOT NULL,\n" +
                "PRIMARY KEY (`authToken`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
        };
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO " + tableName + " (authToken, username) VALUES (?, ?)";
        updateDatabase(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM " + tableName + " WHERE authToken=?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1,authToken);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        return new AuthData(authToken, username);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM " + tableName + " WHERE authToken=?";
        updateDatabase(statement, authToken);
    }

    @Override
    public void deleteAllAuths() throws DataAccessException {
        var statement = "TRUNCATE " + tableName;
        updateDatabase(statement);
    }
}
