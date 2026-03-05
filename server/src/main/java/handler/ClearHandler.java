package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.UserService;

public class ClearHandler {

    public ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public void handle(Context ctx){
        try {
            clearService.clearAllDatabases();
        } catch (ResponseException e) {
            ctx.status(e.getStatusCode());
            ctx.json(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ResponseException(500, e.getMessage()).toJson());
        }
    }

}