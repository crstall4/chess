package dataaccess;


import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    void clear() throws ResponseException;
    AuthData createAuth(String username) throws ResponseException;
    AuthData getAuthData(String username) throws ResponseException;
}

