package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        System.out.println("DEBUG: DatabaseManager static initializer starting...");
        try {
            loadPropertiesFromResources();
            System.out.println("DEBUG: Properties loaded successfully");
            System.out.println("DEBUG: databaseName = " + databaseName);
            System.out.println("DEBUG: dbUsername = " + dbUsername);
            System.out.println("DEBUG: connectionUrl = " + connectionUrl);
        } catch (Exception e) {
            System.err.println("DEBUG: Exception during static initialization:");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        System.out.println("DEBUG: createDatabase() called");
        System.out.println("DEBUG: Connection URL: " + connectionUrl);
        System.out.println("DEBUG: Username: " + dbUsername);
        System.out.println("DEBUG: SQL: " + statement);
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            System.out.println("DEBUG: Connection established, executing statement...");
            preparedStatement.executeUpdate();
            System.out.println("DEBUG: Database creation successful");
        } catch (SQLException ex) {
            System.err.println("DEBUG: SQLException during createDatabase()");
            System.err.println("DEBUG: Error Code: " + ex.getErrorCode());
            System.err.println("DEBUG: SQL State: " + ex.getSQLState());
            System.err.println("DEBUG: Error Message: " + ex.getMessage());
            ex.printStackTrace();
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d?allowPublicKeyRetrieval=true&useSSL=false", host, port);
    }
}
