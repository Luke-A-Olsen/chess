package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SqlAuthDAO implements AuthDAO
{
    @Override
    public void clear() throws DataAccessException
    {
        String statement = "DELETE FROM auth";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to clear auth", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException
    {
        if (auth == null || auth.authToken() == null)
        {
            throw new DataAccessException("Error: bad request");
        }

        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            if (ex.getErrorCode() == 1062)
            {
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException("failed to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        String statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, authToken);
            try (var resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    return new AuthData(resultSet.getString("authToken"), resultSet.getString("username"));
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to get auth", ex);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException
    {
        if (authToken == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }

        int deleted;
        String statement = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, authToken);
            deleted = preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to delete auth", ex);
        }
        if (deleted == 0)
        {
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
