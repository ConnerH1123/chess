package dataaccess;

import model.AuthData;

public class MySqlAuthDAO extends SqlDatabase implements AuthDAO{
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
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void deleteAllAuths() {

    }
}
