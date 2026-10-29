package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class SqlUserDAO implements UserDAO
{
    @Override
    public void clear() throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }
}
