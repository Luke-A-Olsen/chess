package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest
{
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService service;

    @BeforeEach
    void setup()
    {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Register Returns Username and Auth Token")
    void registerReturnsUsernameAndAuthToken() throws BadRequestException, AlreadyTakenException, DataAccessException
    {
        RegisterResult result = service.register(new RegisterRequest("user", "password", "user@mail.com"));

        assertEquals("user", result.username());
        assertNotNull(result.authToken());
        assertEquals(new UserData("user", "password", "user@mail.com"), userDAO.getUser("user"));
        assertEquals(new AuthData(result.authToken(), "user"), authDAO.getAuth(result.authToken()));

        RegisterResult other = service.register(new RegisterRequest("other", "password", "other@mail.com"));
        assertNotEquals(result.authToken(), other.authToken());
    }

    @Test
    @DisplayName("Register Rejects a Taken Username")
    void registerRejectsTakenUsername() throws BadRequestException, AlreadyTakenException, DataAccessException
    {
        service.register(new RegisterRequest("user", "password", "user@mail.com"));

        assertThrows(AlreadyTakenException.class, () ->
                service.register(new RegisterRequest("user", "other-password", "other@mail.com")));
        assertEquals(new UserData("user", "password", "user@mail.com"), userDAO.getUser("user"));
    }

    @Test
    @DisplayName("Register Rejects a Missing Field")
    void registerRejectsMissingField()
    {
        RegisterRequest[] requests = {
                null,
                new RegisterRequest(null, "password", "user@mail.com"),
                new RegisterRequest("user", null, "user@mail.com"),
                new RegisterRequest("user", "password", null),
                new RegisterRequest(" ", "password", "user@mail.com")
        };

        for (RegisterRequest request : requests)
        {
            assertThrows(BadRequestException.class, () -> service.register(request));
        }
        assertNull(userDAO.getUser("user"));
    }

    @Test
    @DisplayName("Login Returns a New Auth Token")
    void loginReturnsNewAuthToken() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException
    {
        RegisterResult registered = service.register(new RegisterRequest("user", "password", "user@mail.com"));

        LoginResult result = service.login(new LoginRequest("user", "password"));

        assertEquals("user", result.username());
        assertNotNull(result.authToken());
        assertNotEquals(registered.authToken(), result.authToken());
        assertEquals(new AuthData(result.authToken(), "user"), authDAO.getAuth(result.authToken()));

        LoginResult again = service.login(new LoginRequest("user", "password"));
        assertNotEquals(result.authToken(), again.authToken());
    }

    @Test
    @DisplayName("Login Rejects an Unknown User or Wrong Password")
    void loginRejectsUnknownUserOrWrongPassword() throws BadRequestException, AlreadyTakenException, DataAccessException
    {
        service.register(new RegisterRequest("user", "password", "user@mail.com"));

        assertThrows(UnauthorizedException.class, () -> service.login(new LoginRequest("missing", "password")));
        assertThrows(UnauthorizedException.class, () -> service.login(new LoginRequest("user", "BAD!PASSWORD")));
        assertEquals(new UserData("user", "password", "user@mail.com"), userDAO.getUser("user"));
    }

    @Test
    @DisplayName("Login Rejects a Missing Field")
    void loginRejectsMissingField() throws BadRequestException, AlreadyTakenException, DataAccessException
    {
        service.register(new RegisterRequest("user", "password", "user@mail.com"));
        LoginRequest[] requests = {
                null,
                new LoginRequest(null, "password"),
                new LoginRequest("user", null),
                new LoginRequest("user", " ")
        };

        for (LoginRequest request : requests)
        {
            assertThrows(BadRequestException.class, () -> service.login(request));
        }
    }

    @Test
    @DisplayName("Logout Removes the Auth Token")
    void logoutRemovesAuthToken() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException
    {
        RegisterResult registered = service.register(new RegisterRequest("user", "password", "user@mail.com"));
        LoginResult loggedIn = service.login(new LoginRequest("user", "password"));

        service.logout(registered.authToken());

        assertNull(authDAO.getAuth(registered.authToken()));
        assertEquals(new AuthData(loggedIn.authToken(), "user"), authDAO.getAuth(loggedIn.authToken()));
        assertEquals(new UserData("user", "password", "user@mail.com"), userDAO.getUser("user"));
    }

    @Test
    @DisplayName("Logout Rejects an Unknown Auth Token")
    void logoutRejectsUnknownAuthToken() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException
    {
        RegisterResult registered = service.register(new RegisterRequest("user", "password", "user@mail.com"));
        service.logout(registered.authToken());

        assertThrows(UnauthorizedException.class, () -> service.logout(registered.authToken()));
        assertThrows(UnauthorizedException.class, () -> service.logout(null));
        assertThrows(UnauthorizedException.class, () -> service.logout(" "));
        assertThrows(UnauthorizedException.class, () -> service.logout("missing"));
    }
}
