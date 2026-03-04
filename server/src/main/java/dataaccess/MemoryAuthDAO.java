package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO {
    final private HashMap<Integer, UserData> authTokens = new HashMap<>();
    public void clear() {
        authTokens.clear();
    }
}
