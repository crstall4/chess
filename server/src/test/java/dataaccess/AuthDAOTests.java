package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTests {

    private Object[] getDataAccess(Class<? extends AuthDAO> databaseClass) throws ResponseException {
        AuthDAO authdb;
        UserDAO userdb;
        GameDAO gamedb;

        if (databaseClass.equals(SQLAuthDAO.class)) {
            authdb = new SQLAuthDAO();
            userdb = new SQLUserDAO();
            gamedb = new SQLGameDAO();
        } else {
            authdb = new MemoryAuthDAO();
            userdb = new MemoryUserDAO();
            gamedb = new MemoryGameDAO();        }
        authdb.clear();
        userdb.clear();
        gamedb.clear();
        return new Object[]{authdb, userdb, gamedb};
    }

    //create a user and get their data to make sure that it all worked.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createUserPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);
        UserData user = new UserData("brian", "password", "brian@gmail.com");

        UserData createdUser = db.createUser(user);

        assertNotNull(createdUser);
        assertEquals("brian", createdUser.username());

        UserData retrieved = db.getUserData("brian");
        assertNotNull(retrieved);
        assertEquals("brian", retrieved.username());
    }

    //create the same user twice
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createUserNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);
        UserData user = new UserData("brian", "password", "brian@gmail.com");
        db.createUser(user);
        assertThrows(ResponseException.class, () -> db.createUser(user));
    }

    //create user and get their data.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getUserDataPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));

        UserData user = db.getUserData("brian");
        assertNotNull(user);
        assertEquals("brian", user.username());
    }

    //get a user's data that we didn't create
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getUserDataNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);

        UserData user = db.getUserData("nonExistent");
        assertNull(user);
    }

    //make sure that clear works
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void clearTest(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));
        db.createUser(new UserData("jeff", "password123", "jeff@gmail.com"));
        
        db.clear();
        
        assertNull(db.getUserData("brian"));
    }

    //test getting all users. add 2 make sure there really is 2.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getUsersPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        AuthDAO db = getDataAccess(dbClass);
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));
        db.createUser(new UserData("jeff", "password123", "jeff@gmail.com"));

        HashMap<Integer, UserData> users = db.getUsers();
        assertEquals(2, users.size());
    }
}