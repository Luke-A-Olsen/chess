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
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private GameService service;

    @BeforeEach
    void setup() throws DataAccessException
    {
        authDAO = new MemoryAuthDAO();
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

    @Test
    @DisplayName("Join Game Claims the Requested Color")
    void joinGameClaimsRequestedColor() throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException
    {
        int gameID = service.createGame("token", new CreateGameRequest("test game")).gameID();

        service.joinGame("token", new JoinGameRequest("WHITE", gameID));
        service.joinGame("token", new JoinGameRequest("BLACK", gameID));

        GameData stored = gameDAO.getGame(gameID);
        assertEquals("user", stored.whiteUsername());
        assertEquals("user", stored.blackUsername());
    }

    @Test
    @DisplayName("Join Game Rejects a Taken Color")
    void joinGameRejectsTakenColor() throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException
    {
        authDAO.createAuth(new AuthData("other-token", "other"));
        int gameID = service.createGame("token", new CreateGameRequest("test game")).gameID();
        service.joinGame("token", new JoinGameRequest("BLACK", gameID));

        assertThrows(AlreadyTakenException.class, () -> service.joinGame("other-token", new JoinGameRequest("BLACK", gameID)));
        assertEquals("user", gameDAO.getGame(gameID).blackUsername());
        assertNull(gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    @DisplayName("Join Game Rejects a Bad Request")
    void joinGameRejectsBadRequest() throws BadRequestException, UnauthorizedException, DataAccessException
    {
        int gameID = service.createGame("token", new CreateGameRequest("test game")).gameID();

        assertThrows(BadRequestException.class, () -> service.joinGame("token", null));
        assertThrows(BadRequestException.class, () -> service.joinGame("token", new JoinGameRequest("WHITE", null)));
        assertThrows(BadRequestException.class, () -> service.joinGame("token", new JoinGameRequest(null, gameID)));
        assertThrows(BadRequestException.class, () -> service.joinGame("token", new JoinGameRequest("", gameID)));
        assertThrows(BadRequestException.class, () -> service.joinGame("token", new JoinGameRequest("GREEN", gameID)));
        assertThrows(BadRequestException.class, () -> service.joinGame("token", new JoinGameRequest("WHITE", 999)));
        assertNull(gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    @DisplayName("Join Game Rejects Bad Authentication")
    void joinGameRejectsBadAuthentication() throws BadRequestException, UnauthorizedException, DataAccessException
    {
        int gameID = service.createGame("token", new CreateGameRequest("test game")).gameID();

        assertThrows(UnauthorizedException.class, () -> service.joinGame(null, new JoinGameRequest("WHITE", gameID)));
        assertThrows(UnauthorizedException.class, () -> service.joinGame("missing", new JoinGameRequest("WHITE", gameID)));
        assertNull(gameDAO.getGame(gameID).whiteUsername());
    }
}
