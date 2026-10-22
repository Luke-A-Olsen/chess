package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public Server() {
        this(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO());
    }

    Server(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        UserService userService = new UserService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        GameService gameService = new GameService(authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", registerHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
        javalin.exception(Exception.class, (exception, context) -> {
            context.status(500);
            context.contentType("application/json");
            context.result(ErrorMessage.toJson(exception));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
