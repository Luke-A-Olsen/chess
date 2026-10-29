package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;

public class SqlGameDAO implements GameDAO
{
    @Override
    public void clear() throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public int createGame(GameData game) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException
    {
        throw new DataAccessException("not implemented");
    }
}
