package service;


import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
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
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new ResponseException(400, "Error: Bad Request. Username, password, and email fields all must be filled out.");
        }
        return loginUser(userDAO.createUser(user));
    }

    public void deleteAllDatabases() throws ResponseException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return;
    }

    public AuthData loginUser(UserData loginAttempt) throws ResponseException {
        if(loginAttempt.username() == null || loginAttempt.password() == null){
            throw new ResponseException(400, "Error: Bad Request. Username, password, and email fields all must be filled out.");
        }
        try {
            UserData user = userDAO.getUserData(loginAttempt);
            if(Objects.equals(loginAttempt.password(), user.password())){
                return authDAO.createAuth(user.username());
            }
            else{
                throw new ResponseException(401, "Error: Unauthorized");
            }
        } catch (ResponseException e) {
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }

    public void logoutUser(String auth) throws ResponseException{
        authDAO.deleteAuthData(auth);
    }

    //this is written just for the sake of unit tests
    public HashMap<Integer, UserData> getUsers() throws ResponseException{
        return userDAO.getUsers();
    }


}
