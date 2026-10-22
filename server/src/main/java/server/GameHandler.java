package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import service.JoinGameRequest;
import service.ListGamesResult;

public class GameHandler
{
    private final GameService gameService;
    private final Gson serializer = new Gson();

    public GameHandler(GameService gameService)
    {
        this.gameService = gameService;
    }

    public void listGames(Context context)
    {
        try
        {
            ListGamesResult result = gameService.listGames(context.header("authorization"));
            HttpResponses.ok(context, serializer.toJson(result));
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }

    public void createGame(Context context)
    {
        try
        {
            CreateGameRequest request = serializer.fromJson(context.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(context.header("authorization"), request);
            HttpResponses.ok(context, serializer.toJson(result));
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }

    public void joinGame(Context context)
    {
        try
        {
            JoinGameRequest request = serializer.fromJson(context.body(), JoinGameRequest.class);
            gameService.joinGame(context.header("authorization"), request);
            HttpResponses.ok(context, "{}");
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }
}
