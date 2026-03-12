package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

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

        UserData user = userDAO.getUserData(loginAttempt.username());

            if(user == null){
                throw new ResponseException(401, "Error: Unauthorized");
            }
            if( Server.useSQL && BCrypt.checkpw(loginAttempt.password(), user.password()) ){
                return authDAO.createAuth(user.username());
            }
            if(!Server.useSQL && loginAttempt.password().equals(user.password())) {
                return authDAO.createAuth(user.username());
            }
            throw new ResponseException(401, "Error: Unauthorized");
            
    }
}
