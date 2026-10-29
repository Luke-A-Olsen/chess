package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlUserDAOTest
{
    private SqlUserDAO userDAO;

    @BeforeAll
    static void setupDatabase() throws DataAccessException
    {
        DatabaseManager.configureDatabase();
    }

    @BeforeEach
    void setup() throws DataAccessException
    {
        userDAO = new SqlUserDAO();
        new SqlAuthDAO().clear();
        userDAO.clear();
    }

    @Test
    @DisplayName("Create User Stores a Hashed Password")
    void createUserStoresHashedPassword() throws DataAccessException
    {
        userDAO.createUser(new UserData("user", "password", "user@mail.com"));

        UserData stored = userDAO.getUser("user");
        assertNotNull(stored);
        assertEquals("user", stored.username());
        assertEquals("user@mail.com", stored.email());
        assertNotEquals("password", stored.password());
        assertTrue(BCrypt.checkpw("password", stored.password()));
    }

    @Test
    @DisplayName("Create User Rejects a Duplicate Username")
    void createUserRejectsDuplicateUsername() throws DataAccessException
    {
        userDAO.createUser(new UserData("user", "password", "user@mail.com"));

        DataAccessException exception = assertThrows(DataAccessException.class, () ->
                userDAO.createUser(new UserData("user", "other", "other@mail.com")));
        assertEquals("Error: already taken", exception.getMessage());
        assertTrue(BCrypt.checkpw("password", userDAO.getUser("user").password()));
    }

    @Test
    @DisplayName("Get User Returns Null When Missing")
    void getUserReturnsNullWhenMissing() throws DataAccessException
    {
        assertNull(userDAO.getUser("missing"));
    }
}
