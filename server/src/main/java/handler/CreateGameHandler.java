package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.GameData;
import model.UserData;
import service.CreateGameService;
import service.UserService;

import java.util.Map;

public class CreateGameHandler {

    public CreateGameService service;

    public CreateGameHandler(CreateGameService service){
        this.service = service;
    }

    public void handle(Context ctx){
        try{
            GameData game = new Gson().fromJson(ctx.body(), GameData.class);
            String auth = ctx.header("Authorization");
            GameData createdGame = service.createGame(auth, game);
            ctx.status(200);
            ctx.json(Map.of("gameID", createdGame.gameID()));
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