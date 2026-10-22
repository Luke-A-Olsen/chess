package server;

import io.javalin.http.Context;
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
            HttpResponses.ok(context, "{}");
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }
}
