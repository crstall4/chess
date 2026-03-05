package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authTokens.put(authData.authToken(), authData.username());
        return authData;
    }

    @Override
    public void deleteAuthData(String token) throws ResponseException{
        String test = authTokens.get(token);
        if(test == null){
            throw new ResponseException(401, "Error: Unauthorized. that auth token didnt exist");
        }
        else{
            authTokens.remove(token);
        }
    }

    @Override
    public void clear() {
        authTokens.clear();
    }



}
