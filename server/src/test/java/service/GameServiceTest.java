package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceTest
{
    private MemoryGameDAO gameDAO;
    private GameService service;

    @BeforeEach
    void setup() throws DataAccessException
    {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        authDAO.createAuth(new AuthData("token", "user"));
        gameDAO = new MemoryGameDAO();
        service = new GameService(authDAO, gameDAO);
    }

    @Test
    @DisplayName("Create Game Returns an ID and Stores an Empty Game")
    void createGameStoresEmptyGame() throws BadRequestException, UnauthorizedException, DataAccessException
    {
        CreateGameResult result = service.createGame("token", new CreateGameRequest("test game"));
        CreateGameResult other = service.createGame("token", new CreateGameRequest("other game"));

        assertTrue(result.gameID() > 0);
        assertNotEquals(result.gameID(), other.gameID());
        GameData stored = gameDAO.getGame(result.gameID());
        assertEquals("test game", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
        assertInstanceOf(ChessGame.class, stored.game());
    }

    @Test
    @DisplayName("Create Game Rejects a Bad Request")
    void createGameRejectsBadRequest()
    {
        assertThrows(BadRequestException.class, () -> service.createGame("token", null));
        assertThrows(BadRequestException.class, () -> service.createGame("token", new CreateGameRequest(null)));
        assertThrows(BadRequestException.class, () -> service.createGame("token", new CreateGameRequest(" ")));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    @DisplayName("Create Game Rejects Bad Authentication")
    void createGameRejectsBadAuthentication()
    {
        assertThrows(UnauthorizedException.class, () -> service.createGame(null, new CreateGameRequest("test game")));
        assertThrows(UnauthorizedException.class, () -> service.createGame("missing", new CreateGameRequest("test game")));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    @DisplayName("List Games Returns Every Game")
    void listGamesReturnsEveryGame() throws BadRequestException, UnauthorizedException, DataAccessException
    {
        assertTrue(service.listGames("token").games().isEmpty());

        int first = service.createGame("token", new CreateGameRequest("first")).gameID();
        int second = service.createGame("token", new CreateGameRequest("second")).gameID();
        ListGamesResult result = service.listGames("token");

        assertEquals(2, result.games().size());
        assertTrue(result.games().contains(new GameSummary(first, null, null, "first")));
        assertTrue(result.games().contains(new GameSummary(second, null, null, "second")));
    }

    @Test
    @DisplayName("List Games Rejects Bad Authentication")
    void listGamesRejectsBadAuthentication()
    {
        assertThrows(UnauthorizedException.class, () -> service.listGames(null));
        assertThrows(UnauthorizedException.class, () -> service.listGames(" "));
        assertThrows(UnauthorizedException.class, () -> service.listGames("missing"));
    }
}
