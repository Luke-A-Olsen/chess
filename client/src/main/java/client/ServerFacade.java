package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this("http://localhost:" + port);
    }

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
    }

    public String serverUrl() {
        return serverUrl;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var body = Map.of("username", username, "password", password, "email", email);
        return makeRequest("POST", "/user", body, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var body = Map.of("username", username, "password", password);
        return makeRequest("POST", "/session", body, null, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    URI endpoint(String path) {
        return URI.create(serverUrl + path);
    }

    private <T> T makeRequest(String method, String path, Object body, String authToken, Class<T> responseClass)
            throws ResponseException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(endpoint(path))
                    .method(method, bodyPublisher(body));
            if (body != null) {
                builder.header("Content-Type", "application/json");
            }
            if (authToken != null) {
                builder.header("authorization", authToken);
            }

            HttpResponse<InputStream> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
            throwIfNotSuccessful(response);
            return readBody(response, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private HttpRequest.BodyPublisher bodyPublisher(Object body) {
        if (body == null) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofString(gson.toJson(body));
    }

    private void throwIfNotSuccessful(HttpResponse<InputStream> response) throws ResponseException {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return;
        }
        String message = "Request failed";
        try (InputStream body = response.body()) {
            if (body != null) {
                Map<?, ?> error = gson.fromJson(new InputStreamReader(body, StandardCharsets.UTF_8), Map.class);
                if (error != null && error.get("message") != null) {
                    message = error.get("message").toString();
                    if (message.startsWith("Error: ")) {
                        message = message.substring("Error: ".length());
                    }
                }
            }
        } catch (Exception ignored) {
            // Keep the default message if the body cannot be parsed.
        }
        throw new ResponseException(status, message);
    }

    private <T> T readBody(HttpResponse<InputStream> response, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            response.body().close();
            return null;
        }
        try (InputStream body = response.body();
             InputStreamReader reader = new InputStreamReader(body, StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, responseClass);
        }
    }
}
