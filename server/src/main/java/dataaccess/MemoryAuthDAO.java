package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<Integer, String> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        authTokens.put(username.hashCode(), UUID.randomUUID().toString());
        return getAuthData(username);
    }

    @Override
    public AuthData getAuthData(String username) throws ResponseException {
        try{
            String auth = authTokens.get(username.hashCode());
            if(auth == null){
                throw new ResponseException(401, "Error: Unauthorized");
            }else{
                return new AuthData(auth,username);
            }
        }
        catch(Exception e){
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }

    @Override
    public void clear() {
        authTokens.clear();
    }



}
