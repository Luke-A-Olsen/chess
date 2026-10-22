package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.BadRequestException;
import service.LoginRequest;
import service.LoginResult;
import service.UnauthorizedException;
import service.UserService;

public class LoginHandler
{
    private final UserService userService;
    private final Gson serializer = new Gson();

    public LoginHandler(UserService userService)
    {
        this.userService = userService;
    }

    public void login(Context context)
    {
        try
        {
            LoginRequest request = serializer.fromJson(context.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
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
}
