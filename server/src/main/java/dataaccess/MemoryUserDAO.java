package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.*;


public class MemoryUserDAO implements UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    @Override
    public UserData createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username().hashCode(), user);
        return user;
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public UserData getUserData(UserData logonAttempt) throws ResponseException{
        try{
            UserData user = users.get(logonAttempt.username().hashCode());
            if(user == null){
                throw new ResponseException(401, "Error: Unauthorized");
            }else{
                return user;
            }
        }
        catch(Exception e){
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }
}
