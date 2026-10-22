package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
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
