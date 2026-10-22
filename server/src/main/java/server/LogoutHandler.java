package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UnauthorizedException;
import service.UserService;

public class LogoutHandler
{
    private final UserService userService;

    public LogoutHandler(UserService userService)
    {
        this.userService = userService;
    }

    public void logout(Context context)
    {
        try
        {
            userService.logout(context.header("authorization"));
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
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
