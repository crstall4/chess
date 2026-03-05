package service;


import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData createUser(UserData user) throws ResponseException{
        return loginUser(userDAO.createUser(user));
    }

    public void deleteAllUsers() throws ResponseException {
        userDAO.clear();
        return;
    }

    public AuthData loginUser(UserData loginAttempt) throws ResponseException {
        try {
            UserData user = userDAO.getUserData(loginAttempt);
            if(Objects.equals(loginAttempt.password(), user.password())){
                return authDAO.createAuth(user.username());
            }
            else{
                throw new ResponseException(401, "Error: Unauthorized");
            }
        } catch (ResponseException e) {
            System.out.println(e);
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }


}
