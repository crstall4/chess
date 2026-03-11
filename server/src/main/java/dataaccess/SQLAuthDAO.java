package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    final private HashMap<String, String> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authTokens.put(authData.authToken(), authData.username());
        return authData;
    }

    @Override
    public void deleteAuthData(String token) throws ResponseException{
        confirmAuth(token);
        authTokens.remove(token);
    }

    @Override
    public void confirmAuth(String token) throws ResponseException{
        if(!authTokens.containsKey(token)){
            throw new ResponseException(401, "Error: Unauthorized. that auth token didnt exist");
        }
    }

    @Override
    public void clear() throws ResponseException {
        authTokens.clear();
    }

    @Override
    public String getUsername(String token) throws ResponseException {
        confirmAuth(token);
        return authTokens.get(token);
    }


}
