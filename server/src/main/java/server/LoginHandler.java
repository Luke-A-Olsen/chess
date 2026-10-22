package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.LoginRequest;
import service.LoginResult;
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
            HttpResponses.ok(context, serializer.toJson(result));
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }
}
