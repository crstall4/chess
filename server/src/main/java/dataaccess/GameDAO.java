package dataaccess;


import exception.ResponseException;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    void clear() throws ResponseException;
    GameData createGame(GameData game) throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    HashMap<Integer, GameData> listGames() throws ResponseException;
    void joinGame(String playerColor, int gameId, String username) throws ResponseException;
}

