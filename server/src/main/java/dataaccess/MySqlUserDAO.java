package dataaccess;

import model.UserData;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDAO extends SqlDatabase implements UserDAO {

    private final String tableName;

    public MySqlUserDAO() throws DataAccessException {
        tableName = "user";
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    public MySqlUserDAO(String tableName) throws DataAccessException {
        this.tableName = tableName;
        String[] createStatements = loadStatements();
        configureDatabase(createStatements);
    }

    private String[] loadStatements() {
        return new String[]{"CREATE TABLE IF NOT EXISTS  " + tableName + " (\n" +
                "`username` varchar(256) NOT NULL,\n" +
                "`password` varchar(256) NOT NULL,\n" +
                "`email` varchar(256) NOT NULL,\n" +
                "PRIMARY KEY (`username`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
        };
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO " + tableName + " (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        updateDatabase(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM " + tableName + " WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1,username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        return new UserData(username, password, email);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE " + tableName;
        updateDatabase(statement);
    }
}
