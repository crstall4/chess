package server;

import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.a.ReaderValueEncoder;
import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.*;


public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        LoginService loginService = new LoginService(userDAO, authDAO);
        RegisterService registerService = new RegisterService(userDAO,loginService);
        LogoutService logoutService = new LogoutService(authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        UserService userService = new UserService(userDAO, authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO,authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO,authDAO);

        LoginHandler loginHandler = new LoginHandler(loginService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::handle)
                .delete("/db", clearHandler::handle)
                .post("/session", loginHandler::handle)
                .delete("/session", logoutHandler::handle)
                .post("/game", createGameHandler::handle)
                .get("/game", listGamesHandler::handle);
//        javalin = Javalin.create(config -> config.staticFiles.add("public"))
//                .post("/pet", this::addPet)
//                .get("/pet", this::listPets)
//                .delete("/pet/{id}", this::deletePet)
//                .delete("/pet", this::deleteAllPets)
//                .exception(ResponseException.class, this::exceptionHandler)
//                .ws("/ws", ws -> {
//                    ws.onConnect(webSocketHandler);
//                    ws.onMessage(webSocketHandler);
//                    ws.onClose(webSocketHandler);
//                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
