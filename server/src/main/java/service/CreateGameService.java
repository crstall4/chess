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
        if(game.gameName() == null){
            throw new ResponseException(400, "Error: Bad Request. Username, password, and email fields all must be filled out.");
        }
        return gameDAO.createGame(game);

    }


}
