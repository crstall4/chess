package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData createUser(UserData user) throws ResponseException, DataAccessException{

        return dataAccess.createUser(user);
    }

    public void deleteAllUsers() throws ResponseException, DataAccessException {
        dataAccess.clear();
        return;
    }

    public AuthData loginUser(UserData user) throws ResponseException, DataAccessException {
        return dataAccess.loginUser(user);
    }


}
