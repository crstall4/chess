package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public void clear() {
        games.clear();
    }

    public GameData createGame(GameData game) throws ResponseException {
        GameData newGame = new GameData(nextId++, null, null, game.gameName(), new ChessGame());
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws ResponseException{
        return games;
    }
}
