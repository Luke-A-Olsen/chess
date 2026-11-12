package client;

import java.net.URI;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        this("http://localhost:" + port);
    }

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
    }

    public String serverUrl() {
        return serverUrl;
    }

    URI endpoint(String path) {
        return URI.create(serverUrl + path);
    }
}
