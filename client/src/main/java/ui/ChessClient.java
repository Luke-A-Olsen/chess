package ui;

import client.GameListEntry;
import client.ListGamesResult;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private enum State {
        PRELOGIN,
        POSTLOGIN
    }

    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken;
    private String username;
    private List<GameListEntry> gameList = new ArrayList<>();

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

    private String evalPostlogin(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "help" -> postloginHelp();
            case "quit" -> "quit";
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "play" -> playGame(params);
            case "observe" -> observeGame(params);
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

    private String logout() throws ResponseException {
        server.logout(authToken);
        authToken = null;
        username = null;
        gameList = new ArrayList<>();
        state = State.PRELOGIN;
        return "Logged out.";
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length != 1) {
            return "Expected: create <NAME>";
        }
        server.createGame(authToken, params[0]);
        return "Created game \"" + params[0] + "\".";
    }

    private String listGames() throws ResponseException {
        ListGamesResult result = server.listGames(authToken);
        gameList = new ArrayList<>(result.games());
        if (gameList.isEmpty()) {
            return "No games available.";
        }

        StringBuilder output = new StringBuilder("Games:\n");
        for (int i = 0; i < gameList.size(); i++) {
            GameListEntry game = gameList.get(i);
            String white = game.whiteUsername() == null ? "(empty)" : game.whiteUsername();
            String black = game.blackUsername() == null ? "(empty)" : game.blackUsername();
            output.append(String.format("  %d. %s\tWhite: %s\tBlack: %s%n",
                    i + 1, game.gameName(), white, black));
        }
        return output.toString().stripTrailing();
    }

    private String playGame(String[] params) throws ResponseException {
        if (params.length != 2) {
            return "Expected: play <LIST_NUMBER> <WHITE|BLACK>";
        }

        Integer listNumber = parsePositiveInt(params[0]);
        if (listNumber == null) {
            return "Game number must be a positive integer.";
        }

        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Player color must be WHITE or BLACK.";
        }

        GameListEntry game = gameFromListNumber(listNumber);
        if (game == null) {
            return "Invalid game number. Run 'list' and choose a valid number.";
        }

        server.joinGame(authToken, color, game.gameID());
        return "Joined \"" + game.gameName() + "\" as " + color + ".";
    }

    private String observeGame(String[] params) {
        if (params.length != 1) {
            return "Expected: observe <LIST_NUMBER>";
        }

        Integer listNumber = parsePositiveInt(params[0]);
        if (listNumber == null) {
            return "Game number must be a positive integer.";
        }

        GameListEntry game = gameFromListNumber(listNumber);
        if (game == null) {
            return "Invalid game number. Run 'list' and choose a valid number.";
        }

        return "Observing \"" + game.gameName() + "\".";
    }

    private GameListEntry gameFromListNumber(int listNumber) {
        if (listNumber < 1 || listNumber > gameList.size()) {
            return null;
        }
        return gameList.get(listNumber - 1);
    }

    private Integer parsePositiveInt(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0 ? number : null;
        } catch (NumberFormatException ex) {
            return null;
        }
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
                  help                      - show this help text
                  logout                    - log out and return to the prelogin menu
                  create <NAME>             - create a new game
                  list                      - list existing games
                  play <#> <WHITE|BLACK>    - join a game as a player
                  observe <#>               - observe a game
                  quit                      - exit the program
                """;
    }
}
