package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class RegisterService {

    private final UserDAO userDAO;
    private final LoginService loginService;

    public RegisterService(UserDAO userDAO, LoginService loginService) {
        this.userDAO = userDAO;
        this.loginService = loginService;
    }

    public AuthData registerUser(UserData user) throws ResponseException{
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new ResponseException(400, "Error: Bad Request. Username, password, and email fields all must be filled out.");
        }
        if(userDAO.getUserData(user.username()) != null){
            throw new ResponseException(403, "Error: The username " + user.username() + " is already taken");
        }
        userDAO.createUser(user);
        return loginService.loginUser(user);
    }
}
