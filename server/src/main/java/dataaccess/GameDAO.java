package dataaccess;


import exception.ResponseException;
import model.GameData;

public interface GameDAO {
    void clear() throws ResponseException;
    GameData createGame(GameData game) throws ResponseException;
}

