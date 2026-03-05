package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData createUser(UserData user) throws ResponseException, DataAccessException{

        return dataAccess.createUser(user);
    }

    public void deleteAllUsers() throws ResponseException, DataAccessException {
        dataAccess.clear();
        return;
    }

    public AuthData loginUser(UserData loginAttempt) throws ResponseException, DataAccessException {
        try {
            UserData user = dataAccess.getUserData(loginAttempt);
            if(Objects.equals(loginAttempt.password(), user.password())){
                return new AuthData(UUID.randomUUID().toString(), user.username());
            }
            else{
                throw new ResponseException(401, "Unauthorized");
            }
        } catch (ResponseException e) {
            throw new ResponseException(401, "Unauthorized");
        }
    }


}
