package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class GameService {

    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteAllUsers() throws ResponseException, DataAccessException {
        dataAccess.clear();
        return;
    }
}
