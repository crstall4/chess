package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

public class UserHandler {

    public UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void handleRegister(Context ctx){
        try{
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            user = userService.createUser(user);
            ctx.result(new Gson().toJson(user));
        } catch (ResponseException e) {
            ctx.status(e.getStatusCode());
            ctx.json(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ResponseException(500, e.getMessage()).toJson());
        }
    }

    public void handleClear(Context ctx){
        userService.deleteAllUsers();
    }

    public void handleLogin(Context ctx){

    }
}