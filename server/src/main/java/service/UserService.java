package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService
{
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO)
    {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException
    {
        if (request == null || missing(request.username()) || missing(request.password()) || missing(request.email()))
        {
            throw new BadRequestException();
        }
        if (userDAO.getUser(request.username()) != null)
        {
            throw new AlreadyTakenException();
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));
        return new RegisterResult(request.username(), createAuth(request.username()));
    }

    public LoginResult login(LoginRequest request) throws BadRequestException, UnauthorizedException, DataAccessException
    {
        if (request == null || missing(request.username()) || missing(request.password()))
        {
            throw new BadRequestException();
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null || !passwordMatches(request.password(), user.password()))
        {
            throw new UnauthorizedException();
        }
        return new LoginResult(request.username(), createAuth(request.username()));
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException
    {
        if (missing(authToken) || authDAO.getAuth(authToken) == null)
        {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authToken);
    }

    private String createAuth(String username) throws DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, username));
        return authToken;
    }

    private static boolean missing(String value)
    {
        return value == null || value.isBlank();
    }

    private static boolean passwordMatches(String provided, String stored)
    {
        if (stored != null && stored.startsWith("$2"))
        {
            return BCrypt.checkpw(provided, stored);
        }
        return provided.equals(stored);
    }
}
