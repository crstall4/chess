package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import handler.UserHandler;
import io.javalin.*;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;


public class Server {
    private final UserService service;
    private final Javalin javalin;
    private UserHandler userHandler;

    public Server() {
        this.service = new UserService(new MemoryDataAccess());
        loginhandler = new LoginHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::createUser)
                .delete("/db", this::deleteAllUsers)
                .post("/session", this::loginUser);
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


    private void createUser(Context ctx) throws ResponseException, DataAccessException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        user = service.createUser(user);
        ctx.result(new Gson().toJson(user));
    }

    private void deleteAllUsers(Context ctx) throws ResponseException, DataAccessException {
        service.deleteAllUsers();
        ctx.status(204);
    }

    private void loginUser(Context ctx) throws ResponseException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        return service.loginUser(user);
    }
}
