package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MySqlUserDAOTest {
    private static DatabaseManager db;


    @Test
    public void testCreateUser() {
        try {
            UserDAO userDAO = new MySqlUserDAO();
            UserData userData = new UserData("joe", "joe mama", "xD");
            userDAO.createUser(userData);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDeleteAllUsers() {
        try {
            UserDAO userDAO = new MySqlUserDAO();
            UserData userData = new UserData("Bob", "password", "email");
            userDAO.createUser(userData);
            userData = new UserData("Sue", "password", "email");
            userDAO.createUser(userData);
            userData = new UserData("Henry", "password", "email");
            userDAO.createUser(userData);
            userDAO.deleteAllUsers();
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
