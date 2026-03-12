package dataaccess;


import exception.ResponseException;
import model.*;
import org.eclipse.jetty.server.Response;

import java.util.HashMap;

public interface UserDAO {
    void clear() throws ResponseException;

    UserData createUser(UserData user) throws ResponseException;

    UserData getUserData(String username) throws ResponseException;

    HashMap<Integer, UserData> getUsers() throws ResponseException;
}

