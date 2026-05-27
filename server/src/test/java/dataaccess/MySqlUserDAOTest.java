package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

public class MySqlUserDAOTest {
    private static DatabaseManager db;
    private UserDAO userDAO;

    @AfterEach
    public void tearDown() {
        try {
            userDAO.deleteAllUsers();
        } catch (Exception e) {
            //
        }

    }

    @Test
    public void testCreateUser() {
        try {
            userDAO = new MySqlUserDAO();
            UserData userData = new UserData("joe", "joe mama", "xD");
            userDAO.createUser(userData);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetUser() {
        try {
            userDAO = new MySqlUserDAO();
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
    public void testDeleteAllUsers() {
        try {
            userDAO = new MySqlUserDAO();
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
