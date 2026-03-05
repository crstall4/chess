package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

public class LoginHandler {

    public UserService userService;

    public LoginHandler(UserService userService){
        this.userService = userService;
    }

    public void handle(Context ctx){
        try{
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            if(user.username() == null || user.password() == null){
                throw new ResponseException(400, "Error: Bad Request");
            }
            Object auth = userService.loginUser(user);
            ctx.result(new Gson().toJson(auth));
        } catch (ResponseException e) {
            System.out.println(e.getStatusCode());
            ctx.status(e.getStatusCode());
            ctx.json(e.toJson());
        } catch (Exception e) {
            System.out.println(e);
            ctx.status(500);
            ctx.json(new ResponseException(500, e.getMessage()).toJson());
        }
    }
}