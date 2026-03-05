package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.GameData;
import model.UserData;
import service.CreateGameService;
import service.ListGamesService;
import service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ListGamesHandler {

    public ListGamesService service;

    public ListGamesHandler(ListGamesService service){
        this.service = service;
    }

    public void handle(Context ctx){
        try{
            String auth = ctx.header("Authorization");
            Collection<GameData> games = service.listGames(auth);
            ctx.status(200);
            ctx.json(Map.of("games", games));
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