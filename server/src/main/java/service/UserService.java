package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData createUser(UserData user) throws DataAccessException {

        return dataAccess.createUser(user);
    }

    public void deleteAllUsers() throws DataAccessException {
        dataAccess.clear();
        return;
    }


}
