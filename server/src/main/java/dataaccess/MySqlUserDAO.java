package dataaccess;

import model.UserData;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDAO extends SqlDatabase implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        updateDatabase(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUser(String username) {

        return null;
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE user";
        updateDatabase(statement);
    }
}
