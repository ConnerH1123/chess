package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySqlGameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.deleteAllGames();
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

    @Test
    public void testListGames() {
        try {
            gameDAO.createGame("game1");
            gameDAO.createGame("game2");
            gameDAO.createGame("game3");
            gameDAO.createGame("game4");
            GameData[] games = gameDAO.listGames();
            Assertions.assertEquals(4, games.length);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testDeleteAllGames() {
        try {
            gameDAO.createGame("game1");
            gameDAO.createGame("game2");
            gameDAO.createGame("game3");
            gameDAO.createGame("game4");
            gameDAO.deleteAllGames();
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
