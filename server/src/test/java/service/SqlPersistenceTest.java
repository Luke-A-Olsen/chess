package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlGameDAO;
import dataaccess.sql.SqlUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlPersistenceTest
{
    private SqlUserDAO userDAO;
    private SqlGameDAO gameDAO;
    private SqlAuthDAO authDAO;

    @BeforeEach
    void setup() throws DataAccessException
    {
        DatabaseManager.configureDatabase();
        userDAO = new SqlUserDAO();
        gameDAO = new SqlGameDAO();
        authDAO = new SqlAuthDAO();
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    @DisplayName("Login Accepts the Hashed Password")
    void loginAcceptsHashedPassword() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException
    {
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(new RegisterRequest("user", "password", "user@mail.com"));

        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("user", "wrong")));
        LoginResult login = userService.login(new LoginRequest("user", "password"));

        assertNotNull(login.authToken());
        assertNotEquals("password", userDAO.getUser("user").password());
        assertTrue(BCrypt.checkpw("password", userDAO.getUser("user").password()));
    }

    @Test
    @DisplayName("Clear Removes Games and Auth Before Users")
    void clearRemovesDependentRows() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException
    {
        UserService userService = new UserService(userDAO, authDAO);
        RegisterResult registered = userService.register(new RegisterRequest("user", "password", "user@mail.com"));
        GameService gameService = new GameService(authDAO, gameDAO);
        CreateGameResult created = gameService.createGame(registered.authToken(), new CreateGameRequest("game"));
        gameService.joinGame(registered.authToken(), new JoinGameRequest("WHITE", created.gameID()));

        new ClearService(userDAO, gameDAO, authDAO).clear();

        assertNull(userDAO.getUser("user"));
        assertNull(authDAO.getAuth(registered.authToken()));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
