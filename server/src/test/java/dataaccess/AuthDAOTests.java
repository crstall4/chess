package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTests {

    private record DataAccessSet(AuthDAO authDAO, UserDAO userDAO) {}

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


    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        AuthData auth = authdb.createAuth("clayton");
        
        assertNotNull(auth.authToken());
        assertEquals("clayton", auth.username());
    }

    //
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createAuthNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];

        assertThrows(ResponseException.class, () -> authdb.createAuth(null));
    }

    //create auth with username, and then get the username from that auth.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getUsernamePositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        AuthData auth = authdb.createAuth("clayton");
        
        String username = authdb.getUsername(auth.authToken());
        assertEquals("clayton", username);
    }

    //try to get username with bad token.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void getUsernameNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];

        assertThrows(ResponseException.class, () -> authdb.getUsername("asdf"));
    }

    //create auth and then delete it, and then try to get the username from that auth.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        AuthData auth = authdb.createAuth("clayton");

        authdb.deleteAuthData(auth.authToken());

        assertThrows(ResponseException.class, () -> authdb.getUsername(auth.authToken()));
    }

    //create auth and then delete it, then try to delete it again.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuthNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        AuthData auth = authdb.createAuth("clayton");

        authdb.deleteAuthData(auth.authToken());

        assertThrows(ResponseException.class, () -> authdb.deleteAuthData(auth.authToken()));
    }

    //create auth and then confirm it, should not throw. then confirm a fake token, should throw.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void confirmAuthPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        AuthData auth = authdb.createAuth("testUser");

        assertDoesNotThrow(() -> authdb.confirmAuth(auth.authToken()));
    }

    //confirm a fake token, should throw.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void confirmAuthNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];

        assertThrows(ResponseException.class, () -> authdb.confirmAuth("fakeToken"));
    }

    //create auths, clear them, and then try to get the username from one of those auths.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void clearTest(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        AuthDAO authdb = (AuthDAO) set[0];
        authdb.createAuth("clayton");
        authdb.createAuth("brian");

        authdb.clear();

        assertThrows(ResponseException.class, () -> authdb.getUsername("clayton"));
    }
}