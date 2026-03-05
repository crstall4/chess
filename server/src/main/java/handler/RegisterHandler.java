package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.RegisterService;
import service.UserService;

public class RegisterHandler {

    public RegisterService service;

    public RegisterHandler(RegisterService service){
        this.service = service;
    }

    public void handle(Context ctx){
        try{
            UserData user = new Gson().fromJson(ctx.body(), UserData.class);
            AuthData auth = service.registerUser(user);
            ctx.result(new Gson().toJson(auth));
        } catch (ResponseException e) {
            ctx.status(e.getStatusCode());
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new ResponseException(500, e.getMessage()).toJson());
        }
    }


}