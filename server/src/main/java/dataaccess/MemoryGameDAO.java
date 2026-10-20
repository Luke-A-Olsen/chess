package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO
{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    @Override
    public void clear()
    {
        games.clear();
        nextID = 1;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException
    {
        if (game == null)
        {
            throw new DataAccessException("Error: bad request");
        }
        int id = nextID++;
        GameData stored = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(id, stored);
        return id;
    }

    @Override
    public GameData getGame(int gameID)
    {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames()
    {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException
    {
        if (game == null || !games.containsKey(game.gameID()))
        {
            throw new DataAccessException("Error: bad request");
        }
        games.put(game.gameID(), game);
    }
}
