package dataaccess;


import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    void clear() throws ResponseException;
    AuthData createAuth(String username) throws ResponseException;
    void deleteAuthData(String token) throws ResponseException;

}

