package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;

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
        String statement = "SELECT password, email FROM " + tableName + " WHERE username=?";
        ArrayList<String[]> queryResponse = queryDatabase(statement, 2, username);
        if (queryResponse.isEmpty()) {
            return null;
        }
        String password = queryResponse.getFirst()[0];
        String email = queryResponse.getFirst()[1];
        return new UserData(username, password, email);
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE " + tableName;
        updateDatabase(statement);
    }
}
