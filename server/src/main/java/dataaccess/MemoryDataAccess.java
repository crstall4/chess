package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.*;


public class MemoryDataAccess implements DataAccess {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    public UserData createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username().hashCode(), user);
        return user;
    }

    public void clear() {
        users.clear();
    }

    public AuthData loginUser(UserData logonAttempt){
        try{
            users.get(logonAttempt.username().hashCode());
        }catch(Exception e){
            throw new ResponseException(ResponseException.Code.ClientError, "Unauthorized");
        }
    }
}
