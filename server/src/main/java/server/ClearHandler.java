package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler
{
    private final ClearService clearService;

    public ClearHandler(ClearService clearService)
    {
        this.clearService = clearService;
    }

    public void clear(Context context)
    {
        try
        {
            clearService.clear();
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        }
        catch (DataAccessException exception)
        {
            context.status(500);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        }
    }
}
