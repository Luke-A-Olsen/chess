package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTest
{
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private ClearService service;

    @BeforeEach
    void setup()
    {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    @DisplayName("Clear Removes Users, Games, and Auth Tokens")
    void clearRemovesStoredData() throws DataAccessException
    {
        userDAO.createUser(new UserData("user", "password", "user@mail.com"));
        gameDAO.createGame(new GameData(0, null, null, "test game", new ChessGame()));
        authDAO.createAuth(new AuthData("token", "user"));

        service.clear();

        assertNull(userDAO.getUser("user"));
        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(authDAO.getAuth("token"));
    }

    @Test
    @DisplayName("Clear Fails When Data Access Fails")
    void clearThrowsWhenDataAccessFails()
    {
        ClearService failingService = new ClearService(new FailingUserDAO(), gameDAO, authDAO);

        assertThrows(DataAccessException.class, failingService::clear);
    }

    private static class FailingUserDAO implements UserDAO
    {
        @Override
        public void clear() throws DataAccessException
        {
            throw new DataAccessException("Error: database unavailable");
        }

        @Override
        public void createUser(UserData user)
        {
        }

        @Override
        public UserData getUser(String username)
        {
            return null;
        }
    }
}
