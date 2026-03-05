package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.UserData;
import service.LoginService;
import service.UserService;

public class LoginHandler {

    public LoginService loginService;

    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }

    public void handle(Context ctx){
        try{
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            Object auth = loginService.loginUser(user);
            ctx.result(new Gson().toJson(auth));
        } catch (ResponseException e) {
            System.out.println(e.getStatusCode());
            ctx.status(e.getStatusCode());
            ctx.result(e.toJson());
        } catch (Exception e) {
            System.out.println(e);
            ctx.status(500);
            ctx.result(new ResponseException(500, e.getMessage()).toJson());
        }
    }
}