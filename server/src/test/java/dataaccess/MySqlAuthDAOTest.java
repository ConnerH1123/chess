package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlAuthDAOTest {
    private static DatabaseManager db;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySqlAuthDAO();
    }

    @Test
    public void testCreateAuth() {
        try {
            AuthData authData = new AuthData("authToken", "username");
            authDAO.createAuth(authData);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
