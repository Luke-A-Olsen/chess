package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlGameDAOTest
{
    private SqlGameDAO gameDAO;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        DatabaseManager.configureDatabase();
    }

    @BeforeEach
    void setup() throws DataAccessException
    {
        gameDAO = new SqlGameDAO();
        gameDAO.clear();
    }

    @Test
    @DisplayName("Create Game Stores the Starting Board")
    void createGameStoresStartingBoard() throws DataAccessException
    {
        ChessGame chessGame = new ChessGame();
        int gameID = gameDAO.createGame(new GameData(0, null, null, "test game", chessGame));

        GameData stored = gameDAO.getGame(gameID);
        assertEquals(gameID, stored.gameID());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertEquals("test game", stored.gameName());
        assertEquals(chessGame, stored.game());
    }

    @Test
    @DisplayName("List Games Returns Every Game")
    void listGamesReturnsEveryGame() throws DataAccessException
    {
        int firstID = gameDAO.createGame(new GameData(0, null, null, "first", new ChessGame()));
        int secondID = gameDAO.createGame(new GameData(0, null, null, "second", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(game -> game.gameID() == firstID && game.gameName().equals("first")));
        assertTrue(games.stream().anyMatch(game -> game.gameID() == secondID && game.gameName().equals("second")));
    }

    @Test
    @DisplayName("Create Game Rejects a Missing Game")
    void createGameRejectsMissingGame()
    {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
        assertEquals("Error: bad request", exception.getMessage());
    }

    @Test
    @DisplayName("Get Game Returns Null When Missing")
    void getGameReturnsNullWhenMissing() throws DataAccessException
    {
        assertNull(gameDAO.getGame(Integer.MAX_VALUE));
    }
}
