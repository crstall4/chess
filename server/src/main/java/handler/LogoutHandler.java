package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

public class LogoutHandler {

    public UserService userService;

    public LogoutHandler(UserService userService){
        this.userService = userService;
    }

    public void handle(Context ctx){
        try{
            String auth = ctx.header("Authorization");
            userService.logoutUser(auth);
            ctx.status(200);
            ctx.result("{}");
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