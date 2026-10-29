package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO
{
    @Override
    public void clear() throws DataAccessException
    {
        String statement = "DELETE FROM users";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to clear users", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException
    {
        if (user == null || user.username() == null)
        {
            throw new DataAccessException("Error: bad request");
        }

        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            if (ex.getErrorCode() == 1062)
            {
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException("failed to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        String statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, username);
            try (var resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    return new UserData(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to get user", ex);
        }
        return null;
    }
}
