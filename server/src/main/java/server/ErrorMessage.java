package server;

import com.google.gson.Gson;

public record ErrorMessage(String message)
{
    private static final Gson SERIALIZER = new Gson();

    public static String toJson(Exception exception)
    {
        String description = exception.getMessage();
        if (description == null || description.isBlank())
        {
            description = "unknown";
        }
        if (!description.startsWith("Error:"))
        {
            description = "Error: " + description;
        }
        return SERIALIZER.toJson(new ErrorMessage(description));
    }
}
