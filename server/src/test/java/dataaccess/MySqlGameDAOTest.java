package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlGameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
    }

    @Test
    public void testCreateGame() {
        try {
            gameDAO.createGame("gameName");
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
