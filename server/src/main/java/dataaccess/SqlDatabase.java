package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDatabase {
    void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    void updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new DataAccessException("Error: invalid parameter(s) passed");
                    }
                }
                ps.executeUpdate();
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

//    void queryDatabase(String statement) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (var preparedStatement = conn.prepareStatement("SELECT id, name, type FROM pet WHERE type=?")) {
//                preparedStatement.setString(1, findType);
//                try (var rs = preparedStatement.executeQuery()) {
//                    while (rs.next()) {
//                        var id = rs.getInt("id");
//                        var name = rs.getString("name");
//                        var type = rs.getString("type");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new DataAccessException(String.format("unable to query database: %s, %s", statement, e.getMessage()));
//        }
//    }

}
