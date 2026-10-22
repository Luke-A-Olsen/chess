package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService
{
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO)
    {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws BadRequestException, UnauthorizedException, DataAccessException
    {
        requireAuth(authToken);
        if (request == null || request.gameName() == null || request.gameName().isBlank())
        {
            throw new BadRequestException();
        }
        int gameID = gameDAO.createGame(new GameData(0, null, null, request.gameName(), new ChessGame()));
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException
    {
        requireAuth(authToken);
        Collection<GameSummary> games = new ArrayList<>();
        for (GameData game : gameDAO.listGames())
        {
            games.add(new GameSummary(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return new ListGamesResult(games);
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException
    {
        String username = requireAuth(authToken);
        if (request == null || request.gameID() == null || !validColor(request.playerColor()))
        {
            throw new BadRequestException();
        }
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null)
        {
            throw new BadRequestException();
        }
        if (request.playerColor().equals("WHITE"))
        {
            if (game.whiteUsername() != null)
            {
                throw new AlreadyTakenException();
            }
            gameDAO.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        }
        else
        {
            if (game.blackUsername() != null)
            {
                throw new AlreadyTakenException();
            }
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        }
    }

    private String requireAuth(String authToken) throws UnauthorizedException, DataAccessException
    {
        if (authToken == null || authToken.isBlank())
        {
            throw new UnauthorizedException();
        }
        var auth = authDAO.getAuth(authToken);
        if (auth == null)
        {
            throw new UnauthorizedException();
        }
        return auth.username();
    }

    private static boolean validColor(String playerColor)
    {
        return "WHITE".equals(playerColor) || "BLACK".equals(playerColor);
    }
}
