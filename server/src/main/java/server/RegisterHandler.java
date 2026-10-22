package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class RegisterHandler
{
    private final UserService userService;
    private final Gson serializer = new Gson();

    public RegisterHandler(UserService userService)
    {
        this.userService = userService;
    }

    public void register(Context context)
    {
        try
        {
            RegisterRequest request = serializer.fromJson(context.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            HttpResponses.ok(context, serializer.toJson(result));
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }
}
