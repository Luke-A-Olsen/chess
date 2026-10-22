package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import service.JoinGameRequest;
import service.ListGamesResult;
import service.UnauthorizedException;

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
            context.status(200);
            context.contentType("application/json");
            context.result(serializer.toJson(result));
        }
        catch (UnauthorizedException exception)
        {
            context.status(401);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
        catch (DataAccessException exception)
        {
            context.status(500);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
    }

    public void createGame(Context context)
    {
        try
        {
            CreateGameRequest request = serializer.fromJson(context.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(context.header("authorization"), request);
            context.status(200);
            context.contentType("application/json");
            context.result(serializer.toJson(result));
        }
        catch (JsonSyntaxException | BadRequestException exception)
        {
            context.status(400);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(new BadRequestException()));
        }
        catch (UnauthorizedException exception)
        {
            context.status(401);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
        catch (DataAccessException exception)
        {
            context.status(500);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
    }

    public void joinGame(Context context)
    {
        try
        {
            JoinGameRequest request = serializer.fromJson(context.body(), JoinGameRequest.class);
            gameService.joinGame(context.header("authorization"), request);
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        }
        catch (JsonSyntaxException | BadRequestException exception)
        {
            context.status(400);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(new BadRequestException()));
        }
        catch (UnauthorizedException exception)
        {
            context.status(401);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
        catch (AlreadyTakenException exception)
        {
            context.status(403);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
        catch (DataAccessException exception)
        {
            context.status(500);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
    }
}
