package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    void registerPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertEquals("player1", auth.username());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(ResponseException.class, () ->
                facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        assertEquals("player1", auth.username());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void loginNegative() {
        assertThrows(ResponseException.class, () ->
                facade.login("missing", "password"));
    }

    @Test
    void logoutPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(ResponseException.class, () -> facade.logout("bad-token"));
    }

    @Test
    void createGamePositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        CreateGameResult result = facade.createGame(auth.authToken(), "fun game");
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(ResponseException.class, () ->
                facade.createGame("bad-token", "fun game"));
    }

    @Test
    void listGamesPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.authToken(), "game one");
        facade.createGame(auth.authToken(), "game two");

        ListGamesResult result = facade.listGames(auth.authToken());
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesNegative() {
        assertThrows(ResponseException.class, () -> facade.listGames("bad-token"));
    }

    @Test
    void joinGamePositive() throws Exception {
        AuthData white = facade.register("white", "password", "w@email.com");
        AuthData black = facade.register("black", "password", "b@email.com");
        int gameID = facade.createGame(white.authToken(), "match").gameID();

        assertDoesNotThrow(() -> facade.joinGame(white.authToken(), "WHITE", gameID));
        assertDoesNotThrow(() -> facade.joinGame(black.authToken(), "BLACK", gameID));

        ListGamesResult result = facade.listGames(white.authToken());
        GameListEntry game = result.games().iterator().next();
        assertEquals("white", game.whiteUsername());
        assertEquals("black", game.blackUsername());
    }

    @Test
    void joinGameNegative() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "match").gameID();
        facade.joinGame(auth.authToken(), "WHITE", gameID);

        AuthData other = facade.register("player2", "password", "p2@email.com");
        assertThrows(ResponseException.class, () ->
                facade.joinGame(other.authToken(), "WHITE", gameID));
        assertThrows(ResponseException.class, () ->
                facade.joinGame("bad-token", "BLACK", gameID));
    }

    @Test
    void clearPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.authToken(), "match");
        facade.clear();

        AuthData again = facade.register("player1", "password", "p1@email.com");
        assertTrue(again.authToken().length() > 10);
        assertTrue(facade.listGames(again.authToken()).games().isEmpty());
    }
}
