package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO implements GameDAO
{
    private static final Gson SERIALIZER = new Gson();

    @Override
    public void clear() throws DataAccessException
    {
        String statement = "DELETE FROM games";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to clear games", ex);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException
    {
        if (game == null)
        {
            throw new DataAccessException("Error: bad request");
        }

        int gameID;
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, SERIALIZER.toJson(game.game()));
            preparedStatement.executeUpdate();
            try (var keys = preparedStatement.getGeneratedKeys())
            {
                if (!keys.next())
                {
                    throw new DataAccessException("failed to create game");
                }
                gameID = keys.getInt(1);
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to create game", ex);
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException
    {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setInt(1, gameID);
            try (var resultSet = preparedStatement.executeQuery())
            {
                if (resultSet.next())
                {
                    return readGame(resultSet);
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to get game", ex);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException
    {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement);
             var resultSet = preparedStatement.executeQuery())
        {
            while (resultSet.next())
            {
                games.add(readGame(resultSet));
            }
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to list games", ex);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException
    {
        if (game == null)
        {
            throw new DataAccessException("Error: bad request");
        }

        int updated;
        String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement))
        {
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, SERIALIZER.toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());
            updated = preparedStatement.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException("failed to update game", ex);
        }
        if (updated == 0)
        {
            throw new DataAccessException("Error: bad request");
        }
    }

    private static GameData readGame(ResultSet resultSet) throws SQLException
    {
        ChessGame game = SERIALIZER.fromJson(resultSet.getString("game"), ChessGame.class);
        return new GameData(
                resultSet.getInt("gameID"),
                resultSet.getString("whiteUsername"),
                resultSet.getString("blackUsername"),
                resultSet.getString("gameName"),
                game);
    }
}
