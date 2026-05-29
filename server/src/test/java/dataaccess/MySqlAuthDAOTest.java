package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

public class MySqlAuthDAOTest {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySqlAuthDAO("test");
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        authDAO.deleteAllAuths();
    }

    @AfterAll
    public static void completeTearDown() throws DataAccessException {
        MySqlUserDAOTest.completeTearDown();
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

    @Test
    public void testGetAuth() {
        try {
            String authToken = "authToken";
            String username = "username";
            AuthData expectedData = new AuthData(authToken, username);
            authDAO.createAuth(expectedData);
            AuthData actualData = authDAO.getAuth(authToken);
            Assertions.assertEquals(expectedData.username(), actualData.username());
            Assertions.assertEquals(expectedData.authToken(), actualData.authToken());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDeleteAuth() {
        try {
            AuthData authData = new AuthData("token", "username");
            authDAO.createAuth(authData);
            authData = new AuthData("otherToken", "username");
            authDAO.createAuth(authData);
            authData = new AuthData("lastToken", "username");
            authDAO.createAuth(authData);
            authDAO.deleteAuth("otherToken");
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDeleteAllAuths() {
        try {
            AuthData authData = new AuthData("token", "username");
            authDAO.createAuth(authData);
            authData = new AuthData("otherToken", "username");
            authDAO.createAuth(authData);
            authData = new AuthData("lastToken", "username");
            authDAO.createAuth(authData);
            authDAO.deleteAllAuths();
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
