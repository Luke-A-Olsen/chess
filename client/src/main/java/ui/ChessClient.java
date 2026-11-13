package ui;

import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

import java.util.Arrays;

public class ChessClient {
    private enum State {
        PRELOGIN,
        POSTLOGIN
    }

    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken;
    private String username;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
            if (tokens.length == 1 && tokens[0].isEmpty()) {
                return "";
            }
            var command = tokens[0].toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (state) {
                case PRELOGIN -> evalPrelogin(command, params);
                case POSTLOGIN -> evalPostlogin(command, params);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (Exception ex) {
            return "Unexpected error. Please try again.";
        }
    }

    public String promptLabel() {
        return state == State.PRELOGIN ? "[LOGGED_OUT]" : "[LOGGED_IN]";
    }

    private String evalPrelogin(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "help" -> preloginHelp();
            case "quit" -> "quit";
            case "login" -> login(params);
            case "register" -> register(params);
            default -> "Unknown command. Type 'help' to see available commands.";
        };
    }

    private String evalPostlogin(String command, String[] params) {
        return switch (command) {
            case "help" -> postloginHelp();
            case "quit" -> "quit";
            default -> "Unknown command. Type 'help' to see available commands.";
        };
    }

    private String login(String[] params) throws ResponseException {
        if (params.length != 2) {
            return "Expected: login <username> <password>";
        }
        AuthData auth = server.login(params[0], params[1]);
        authToken = auth.authToken();
        username = auth.username();
        state = State.POSTLOGIN;
        return "Logged in as " + username + ".";
    }

    private String register(String[] params) throws ResponseException {
        if (params.length != 3) {
            return "Expected: register <username> <password> <email>";
        }
        AuthData auth = server.register(params[0], params[1], params[2]);
        authToken = auth.authToken();
        username = auth.username();
        state = State.POSTLOGIN;
        return "Registered and logged in as " + username + ".";
    }

    private String preloginHelp() {
        return """
                Available commands:
                  help                 - show this help text
                  quit                 - exit the program
                  login <u> <p>        - log in with username and password
                  register <u> <p> <e> - create an account and log in
                """;
    }

    private String postloginHelp() {
        return """
                Available commands:
                  help - show this help text
                  quit - exit the program
                More postlogin commands will be added next.
                """;
    }
}
