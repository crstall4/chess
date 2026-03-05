package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class CreateGameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public CreateGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(String token, GameData game) throws ResponseException{
        authDAO.confirmAuth(token);
        return gameDAO.createGame(game);

    }


}
