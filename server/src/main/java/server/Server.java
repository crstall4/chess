package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.LoginHandler;
import handler.RegisterHandler;
import handler.LogoutHandler;
import io.javalin.*;
import service.UserService;


public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO, gameDAO);

        LoginHandler loginHandler = new LoginHandler(userService);
        RegisterHandler registerHandler = new RegisterHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::handle)
                .delete("/db", clearHandler::handle)
                .post("/session", loginHandler::handle)
                .delete("/session", logoutHandler::handle);
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
