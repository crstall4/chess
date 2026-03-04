package dataaccess;


import exception.ResponseException;
import model.*;

public interface DataAccessObject {
    void clear() throws ResponseException;

    UserData createUser(UserData user) throws ResponseException;

    UserData getUserData(UserData user) throws ResponseException;
}

