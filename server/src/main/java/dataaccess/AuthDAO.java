package dataaccess;


import exception.ResponseException;
import model.UserData;

public interface AuthDAO {
    void clear() throws ResponseException;
}

