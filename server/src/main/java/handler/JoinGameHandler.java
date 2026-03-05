package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.http.Context;
import model.GameData;
import model.UserData;
import service.CreateGameService;
import service.JoinGameService;
import service.ListGamesService;
import service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JoinGameHandler {

    public JoinGameService service;

    public JoinGameHandler(JoinGameService service){
        this.service = service;
    }

    public void handle(Context ctx){
        try{
            GameData game = new Gson().fromJson(ctx.body(), GameData.class);
            String auth = ctx.header("Authorization");
            Map<String, Object> request = new Gson().fromJson(ctx.body(), Map.class);
            if(request.get("playerColor") == null || request.get("gameID") == null){
                throw new ResponseException(400, "Error: bad request. you need to select GameID AND player color.");
            }
            String playerColor = (String) request.get("playerColor");
            int gameID = ((Double) request.get("gameID")).intValue();
            service.joinGame(auth, playerColor, gameID);
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