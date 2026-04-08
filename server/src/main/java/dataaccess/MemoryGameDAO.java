package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public GameData createGame(GameData game) throws ResponseException {
        GameData newGame = new GameData(nextId++, null, null, game.gameName(), new ChessGame());
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        return games.get(gameID);
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws ResponseException{
        return games;
    }

    @Override
    public void joinGame(String playerColor, int gameId, String username) throws ResponseException{
        GameData game = games.get(gameId);
        if(game == null){
            throw new ResponseException(400, "Error: Bad Request. That GameId does not exist");
        }
        if(!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK")){
            throw new ResponseException(400, "Error: Bad Request. Choose WHITE or BLACK for team color.");
        }
        if(playerColor.equals("WHITE")){
            if(game.whiteUsername() != null){
                throw new ResponseException(403, "Error: already taken. That game already has a white player.");
            }
            GameData newGame = new GameData(game.gameID(),username,game.blackUsername(),game.gameName(),game.game());
            games.remove(game.gameID());
            games.put(newGame.gameID(),newGame);
        }
        if(playerColor.equals("BLACK")){
            if(game.blackUsername() != null){
                throw new ResponseException(403, "Error: already taken. That game already has a black player.");
            }
            GameData newGame = new GameData(game.gameID(),game.whiteUsername(),username,game.gameName(),game.game());
            games.remove(game.gameID());
            games.put(newGame.gameID(),newGame);
        }
    }

    @Override
    public void updateGame(GameData game) throws ResponseException {
        if (!games.containsKey(game.gameID())) {
            throw new ResponseException(400, "Error: game not found");
        }
        games.put(game.gameID(), game);
    }

}
