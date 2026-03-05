package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class LoginService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
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
}
