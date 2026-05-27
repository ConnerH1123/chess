package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySqlUserDAOTest {
    private static DatabaseManager db;
    private UserDAO userDAO;


    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySqlUserDAO("test");
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDAO.deleteAllUsers();
    }

    @AfterAll
    public static void completeTearDown() throws DataAccessException {
        String statement = "DROP TABLE IF EXISTS test";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Test
    public void testCreateUser() {
        try {
            UserData userData = new UserData("joe", "joe mama", "xD");
            userDAO.createUser(userData);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testCreateUserDuplicate() {
        try {
            UserData userData = new UserData("joe", "joe mama", "xD");
            userDAO.createUser(userData);
            userDAO.createUser(userData);
            Assertions.fail("Shouldn't support duplicate entrees");
        } catch (Exception e) {
            //
        }
    }

    @Test
    public void testGetUser() {
        try {
            String username = "joe";
            String password = "joe mama";
            String email = "xD";
            UserData expectedData = new UserData(username, password, email);
            userDAO.createUser(expectedData);
            UserData actualData = userDAO.getUser(username);
            Assertions.assertEquals(expectedData.username(), actualData.username());
            Assertions.assertEquals(expectedData.email(), actualData.email());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetAbsentUser() {
        try {
            String username = "joe";
            String password = "joe mama";
            String email = "xD";
            UserData userData = new UserData(username, password, email);
            userDAO.createUser(userData);
            Assertions.assertNull(userDAO.getUser("username"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDeleteAllUsers() {
        try {
            UserData userData = new UserData("Bob", "password", "email");
            userDAO.createUser(userData);
            userData = new UserData("Sue", "password", "email");
            userDAO.createUser(userData);
            userData = new UserData("Henry", "password", "email");
            userDAO.createUser(userData);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
