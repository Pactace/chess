package dataAccessTests;

import dataAccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    SQLGameDAO sqlGameDAO = new SQLGameDAO();

    SQLGameDAOTest() throws DataAccessException {
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        sqlGameDAO.clear();
    }
    @Test
    void getTestGame() throws DataAccessException {
        //first we input a game and then we check if its not null
        sqlGameDAO.createGame("test game");
        assertNotEquals(null, sqlGameDAO.getGame(1));
    }

    @Test
    void getGameReturnsNull() throws DataAccessException {
        sqlGameDAO.createGame("test game");
        assertEquals(null, sqlGameDAO.getGame(2));
    }

    @Test
    void createGameThrowsNoExceptions() throws DataAccessException {
        sqlGameDAO.createGame("test game");
        assertNotEquals(null, sqlGameDAO.getGame(1));
    }

    @Test
    void createGameThrowsExceptions() {
        Assertions.assertThrows(DataAccessException.class, () ->   sqlGameDAO.createGame(null));
    }

    @Test
    void whitePlayerJoinGame() throws DataAccessException {
        //here I will be testing to make sure the white player joins the game
        sqlGameDAO.createGame("test game");
        sqlGameDAO.joinGame("WHITE", 1, "whitePlayer");
        GameData gameData = sqlGameDAO.getGame(1);

        Assertions.assertEquals("whitePlayer", gameData.whiteUsername());
    }

    @Test
    void playerTriesToJoinGameWithInvalidId() throws DataAccessException {
        //here I will be testing to make sure the white player joins the game
        sqlGameDAO.createGame("test game");

        Assertions.assertThrows(DataAccessException.class, () ->   sqlGameDAO.joinGame("WHITE", 2, "whitePlayer"));
    }

    @Test
    void listGamesReturnsCorrectAmountOfGames() throws DataAccessException {
        //here we will add 3 games and make sure that they return a list of size 3
        sqlGameDAO.createGame("1");
        sqlGameDAO.createGame("2");
        sqlGameDAO.createGame("3");

        Assertions.assertEquals(3, sqlGameDAO.listGames().size());
    }

    @Test
    void listGamesReturnsNothing() throws DataAccessException {
        Assertions.assertEquals(0, sqlGameDAO.listGames().size());
    }


    @Test
    void clear() throws DataAccessException {
        //here we are just going to make sure that by getting a list of games we will recieve nothing
        //here we will add 3 games and make sure that they return a list of size 3
        sqlGameDAO.createGame("1");
        sqlGameDAO.createGame("2");
        sqlGameDAO.createGame("3");

        Assertions.assertEquals(3, sqlGameDAO.listGames().size());

        sqlGameDAO.clear();

        Assertions.assertEquals(0, sqlGameDAO.listGames().size());
    }
}