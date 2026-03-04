package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.*;


public class MemoryUserDAO implements DataAccessObject {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    public UserData createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username().hashCode(), user);
        return user;
    }

    public void clear() {
        users.clear();
    }

    public UserData getUserData(UserData logonAttempt) throws ResponseException{
        try{
            return users.get(logonAttempt.username().hashCode());
        }catch(Exception e){
            throw new ResponseException(401, "Unauthorized");
        }
    }
}
