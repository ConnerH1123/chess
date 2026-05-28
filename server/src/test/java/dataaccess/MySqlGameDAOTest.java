package dataaccess;

import chess.ChessGame;
import model.GameData;
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

    @Test
    public void testGetGame() {
        try {
            String gameName = "not checkers xD";
            GameData expectedData = new GameData(1, null, null, gameName, new ChessGame());
            gameDAO.createGame(gameName);
            GameData actualData = gameDAO.getGame(1);
            Assertions.assertEquals(expectedData.whiteUsername(), actualData.whiteUsername());
            Assertions.assertEquals(expectedData.blackUsername(), actualData.blackUsername());
            Assertions.assertEquals(expectedData.gameName(), actualData.gameName());
            Assertions.assertEquals(expectedData.game(), actualData.game());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
