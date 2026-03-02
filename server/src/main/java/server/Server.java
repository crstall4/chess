package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import model.UserData;
import service.UserService;
import io.javalin.http.Context;


public class Server {
    private final UserService service;
    private final Javalin javalin;

    public Server(UserService service) {
        this.service = service;
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::createUser)
                .delete("/user", this::deleteAllUsers);
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
    public Server() {
        this(new UserService(new MemoryDataAccess()));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


    private void createUser(Context ctx) throws DataAccessException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        user = service.createUser(user);
        ctx.result(new Gson().toJson(user));
    }

    private void deleteAllUsers(Context ctx) throws DataAccessException {
        service.deleteAllUsers();
        ctx.status(204);
    }
}
