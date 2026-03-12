package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.*;




public class MemoryUserDAO implements UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        user = new UserData(user.username(), user.password(), user.email());

        if(users.get(user.username().hashCode()) != null){
            throw new ResponseException(403, "Error: Forbidden. User already registered.");
        }

        users.put(user.username().hashCode(), user);
        return user;
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public UserData getUserData(String username) throws ResponseException{
        try{
            UserData user = users.get(username.hashCode());
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

    @Override
    public HashMap<Integer, UserData> getUsers() throws ResponseException{
        return users;
    }


}
