package dataaccess;

import model.UserData;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDAO extends SqlDatabase implements UserDAO {

    private final String tableName;

    public MySqlUserDAO() throws DataAccessException {
        System.out.println("DEBUG: MySqlUserDAO() initialization started");
        tableName = "user";
        String[] createStatements = loadStatements();
        System.out.println("DEBUG: Configuring database...");
        configureDatabase(createStatements);
        System.out.println("DEBUG: Database configured!");
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
        System.out.println("DEBUG: createUser() called");
        String statement = "INSERT INTO " + tableName + " (username, password, email) VALUES (?, ?, ?)";
        System.out.println("DEBUG: hashing password...");
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        System.out.println("DEBUG: Password hashed");
        updateDatabase(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        System.out.println("DEBUG: getUser() called");
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
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        System.out.println("DEBUG: deleteAllUsers() called");
        var statement = "TRUNCATE IF EXISTS " + tableName;
        updateDatabase(statement);
    }
}
