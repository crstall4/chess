package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;

public class AuthService {
    private final AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteAllUsers() throws ResponseException, DataAccessException {
        dataAccess.clear();
        return;
    }
}
