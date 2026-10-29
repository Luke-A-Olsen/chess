package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class SqlAuthDAO implements AuthDAO
{
    @Override
    public void clear() throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }
}
