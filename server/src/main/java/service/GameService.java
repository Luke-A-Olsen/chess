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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws BadRequestException, UnauthorizedException, DataAccessException
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

    private void requireAuth(String authToken) throws UnauthorizedException, DataAccessException
    {
        if (authToken == null || authToken.isBlank() || authDAO.getAuth(authToken) == null)
        {
            throw new UnauthorizedException();
        }
    }
}
