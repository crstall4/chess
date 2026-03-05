package dataaccess;


import exception.ResponseException;
import model.GameData;

import java.util.HashMap;

public interface GameDAO {
    void clear() throws ResponseException;
    GameData createGame(GameData game) throws ResponseException;
    HashMap<Integer, GameData> listGames() throws ResponseException;
}

