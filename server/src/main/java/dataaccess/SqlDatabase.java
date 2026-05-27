package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDatabase {
    void configureDatabase(String[] createStatements) throws DataAccessException {
        System.out.println("DEBUG: configureDatabase() called");
        DatabaseManager.createDatabase();
        System.out.println("DEBUG: Database created, now getting connection...");
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("DEBUG: Connection obtained, executing " + createStatements.length + " statements");
            for (String statement : createStatements) {
                System.out.println("DEBUG: Executing: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                    System.out.println("DEBUG: Statement executed successfully");
                }
            }
            System.out.println("DEBUG: All statements executed successfully");
        } catch (SQLException ex) {
            System.err.println("DEBUG: SQLException in configureDatabase()");
            System.err.println("DEBUG: Error Code: " + ex.getErrorCode());
            System.err.println("DEBUG: SQL State: " + ex.getSQLState());
            System.err.println("DEBUG: Error Message: " + ex.getMessage());
            ex.printStackTrace();
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    void updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> preparedStatement.setString(i + 1, p);
                        case Integer p -> preparedStatement.setInt(i + 1, p);
                        case null -> preparedStatement.setNull(i + 1, NULL);
                        default -> throw new DataAccessException("Error: invalid parameter(s) passed");
                    }
                }
                preparedStatement.executeUpdate();
//                Uncomment if I want to return the generated key
//                ResultSet rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
