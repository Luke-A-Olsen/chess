package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import model.UserData;
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
    private SqlUserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        DatabaseManager.configureDatabase();
    }

    @BeforeEach
    void setup() throws DataAccessException
    {
        gameDAO = new SqlGameDAO();
        userDAO = new SqlUserDAO();
        gameDAO.clear();
        new SqlAuthDAO().clear();
        userDAO.clear();
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

    @Test
    @DisplayName("Update Game Stores Players and a Move")
    void updateGameStoresPlayersAndMove() throws DataAccessException, InvalidMoveException
    {
        userDAO.createUser(new UserData("white", "password", "white@mail.com"));
        userDAO.createUser(new UserData("black", "password", "black@mail.com"));
        ChessGame chessGame = new ChessGame();
        int gameID = gameDAO.createGame(new GameData(0, null, null, "test game", chessGame));

        chessGame.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        gameDAO.updateGame(new GameData(gameID, "white", "black", "test game", chessGame));

        GameData stored = gameDAO.getGame(gameID);
        assertEquals("white", stored.whiteUsername());
        assertEquals("black", stored.blackUsername());
        assertEquals(chessGame, stored.game());
        assertEquals(ChessGame.TeamColor.BLACK, stored.game().getTeamTurn());
    }

    @Test
    @DisplayName("Update Game Rejects a Missing Game")
    void updateGameRejectsMissingGame()
    {
        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                gameDAO.updateGame(new GameData(Integer.MAX_VALUE, null, null, "missing", new ChessGame())));
        assertEquals("Error: bad request", exception.getMessage());
    }
}
