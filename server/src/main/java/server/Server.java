package server;

import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import handler.UserHandler;
import io.javalin.*;
import service.UserService;


public class Server {
    private final Javalin javalin;

    public Server() {
        UserService userService = new UserService(new MemoryUserDAO());
        UserHandler userHandler = new UserHandler(userService);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", userHandler::handleRegister)
                .delete("/db", userHandler::handleClear)
                .post("/session", userHandler::handleLogin);
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
