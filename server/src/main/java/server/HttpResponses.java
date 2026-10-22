package server;

import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;

public class HttpResponses
{
    public static void ok(Context context, String body)
    {
        context.status(200);
        context.contentType("application/json");
        context.result(body);
    }

    public static void fail(Context context, Exception exception)
    {
        int status = 500;
        Exception reported = exception;
        if (exception instanceof JsonSyntaxException || exception instanceof BadRequestException)
        {
            status = 400;
            reported = new BadRequestException();
        }
        else if (exception instanceof UnauthorizedException)
        {
            status = 401;
        }
        else if (exception instanceof AlreadyTakenException)
        {
            status = 403;
        }
        context.status(status);
        context.contentType("application/json");
        context.result(ErrorMessage.toJson(reported));
    }
}
