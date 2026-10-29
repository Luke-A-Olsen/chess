package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlAuthDAOTest
{
    private SqlAuthDAO authDAO;
    private SqlUserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        DatabaseManager.configureDatabase();
    }

    @BeforeEach
    void setup() throws DataAccessException
    {
        authDAO = new SqlAuthDAO();
        userDAO = new SqlUserDAO();
        authDAO.clear();
        userDAO.clear();
        userDAO.createUser(new UserData("user", "password", "user@mail.com"));
    }

    @Test
    @DisplayName("Create Auth Stores the Token")
    void createAuthStoresToken() throws DataAccessException
    {
        AuthData auth = new AuthData("token", "user");
        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token"));
    }

    @Test
    @DisplayName("Create Auth Rejects a Duplicate Token")
    void createAuthRejectsDuplicateToken() throws DataAccessException
    {
        authDAO.createAuth(new AuthData("token", "user"));

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                authDAO.createAuth(new AuthData("token", "user")));
        assertEquals("Error: already taken", exception.getMessage());
        assertEquals(new AuthData("token", "user"), authDAO.getAuth("token"));
    }

    @Test
    @DisplayName("Delete Auth Removes the Token")
    void deleteAuthRemovesToken() throws DataAccessException
    {
        authDAO.createAuth(new AuthData("token", "user"));

        authDAO.deleteAuth("token");

        assertNull(authDAO.getAuth("token"));
    }

    @Test
    @DisplayName("Delete Auth Rejects a Missing Token")
    void deleteAuthRejectsMissingToken()
    {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("missing"));
        assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Get Auth Returns Null When Missing")
    void getAuthReturnsNullWhenMissing() throws DataAccessException
    {
        assertNull(authDAO.getAuth("missing"));
    }
}
