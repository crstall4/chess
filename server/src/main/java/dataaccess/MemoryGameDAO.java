package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO {
    final private HashMap<Integer, UserData> games = new HashMap<>();
    public void clear() {
        games.clear();
    }
}
