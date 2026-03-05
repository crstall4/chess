package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

public class RegisterHandler {

    public UserService userService;

    public RegisterHandler(UserService userService){
        this.userService = userService;
    }

    public void handle(Context ctx){
        try{
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            AuthData auth = userService.createUser(user);
            ctx.result(new Gson().toJson(auth));
        } catch (ResponseException e) {
            ctx.status(e.getStatusCode());
            ctx.json(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ResponseException(500, e.getMessage()).toJson());
        }
    }


}