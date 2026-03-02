package dataaccess;

import java.util.HashMap;
import model.*;


public class MemoryDataAccess implements DataAccess {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    public void createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username().hashCode(), user);
        return;
    }

    public void clear() {
        users.clear();
    }
}
