package server;

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
            HttpResponses.ok(context, "{}");
        }
        catch (Exception exception)
        {
            HttpResponses.fail(context, exception);
        }
    }
}
