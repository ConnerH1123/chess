package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;


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
            throw new DataAccessException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    int updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                loadParametersIntoStatement(preparedStatement, params);
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void loadParametersIntoStatement(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> preparedStatement.setString(i + 1, p);
                case Integer p -> preparedStatement.setInt(i + 1, p);
                case null -> preparedStatement.setNull(i + 1, NULL);
                default -> throw new SQLException("Error: invalid parameter(s) passed");
            }
        }
    }

    ArrayList<String[]> queryDatabase(String statement, int columnCount, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                loadParametersIntoStatement(preparedStatement, params);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSetToStringList(resultSet, columnCount);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

    private ArrayList<String[]> resultSetToStringList(ResultSet resultSet, int columnCount) throws SQLException {
        ArrayList<String[]> rows = new ArrayList<>();
        while (resultSet.next()) {
            String[] currentRow = new String[columnCount];
            for (int i = 1; i <= columnCount; i++){
                currentRow[i-1] = resultSet.getString(i);
            }
            rows.add(currentRow);
        }
        return rows;
    }

    int getTableSize(String tableName) throws DataAccessException {
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
            throw new DataAccessException(String.format("Error: unable to query database: %s, %s", statement, e.getMessage()));
        }
    }

}
