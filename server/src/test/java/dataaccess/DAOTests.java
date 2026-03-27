package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import chess.ChessGame;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

class DAOTests {

    private record DataAccessSet(AuthDAO authDAO, UserDAO userDAO) {}

    private Object[] getDataAccess(Class<?> databaseClass) throws ResponseException {
        AuthDAO authdb;
        UserDAO userdb;
        GameDAO gamedb;

        if (databaseClass.equals(SQLAuthDAO.class) || databaseClass.equals(SQLUserDAO.class) || databaseClass.equals(SQLGameDAO.class)) {
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


    //GAMEDAO TESTS BELOW

    //create a game and then get the list of games to make sure it was created.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createGamePositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        GameData created = gamedb.createGame(new GameData(0, null, null, "first", new ChessGame()));

        assertEquals("first", created.gameName());
        assertNull(created.whiteUsername());
        assertNull(created.blackUsername());
    }

    //create a game that is just a null game, meaning it should error out.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void createGameNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        assertThrows(NullPointerException.class, () -> gamedb.createGame(null));
    }

    //create 2 games and then make sure the list of games has both of them.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void listGamesPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        gamedb.createGame(new GameData(0, null, null, "gameoneeee", new ChessGame()));
        gamedb.createGame(new GameData(0, null, null, "gametwo", new ChessGame()));

        HashMap<Integer, GameData> games = gamedb.listGames();
        assertEquals(2, games.size());
    }

    //get the list of games when we haven't created any
    //service class does authentication, so it should be impossible for this to fail. thats why i just had it be empty for the negative case.
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void listGamesNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        HashMap<Integer, GameData> games = gamedb.listGames();
        assertEquals(0, games.size());
    }

    //create game and join it
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void joinGamePositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        GameData created = gamedb.createGame(new GameData(0, null, null, "game1", new ChessGame()));
        gamedb.joinGame("WHITE", created.gameID(), "clayton");

        GameData updated = gamedb.listGames().get(created.gameID());
        assertNotNull(updated);
        assertEquals("clayton", updated.whiteUsername());
        assertNull(updated.blackUsername());
    }

    //try to join game with bad color
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void joinGameNegative(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        GameData created = gamedb.createGame(new GameData(0, null, null, "bad-color", new ChessGame()));

        assertThrows(ResponseException.class, () -> gamedb.joinGame("BLUE", created.gameID(), "clayton"));
    }

    //clear it all out baby
    @ParameterizedTest
    @ValueSource(classes = {SQLAuthDAO.class, MemoryAuthDAO.class})
    void clearPositive(Class<? extends AuthDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        GameDAO gamedb = (GameDAO) set[2];

        gamedb.createGame(new GameData(0, null, null, "one", new ChessGame()));
        gamedb.createGame(new GameData(0, null, null, "two", new ChessGame()));

        gamedb.clear();

        assertEquals(0, gamedb.listGames().size());
    }


    //USERDAO TESTS BELOW

    //create a user and get their data to make sure that it all worked.
    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserPositive(Class<? extends UserDAO> dbClass) throws ResponseException {
        Object[] set = getDataAccess(dbClass);
        UserDAO db = (UserDAO) set[1];
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
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void createUserNegative(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO db = (UserDAO) getDataAccess(dbClass)[1];
        UserData user = new UserData("brian", "password", "brian@gmail.com");
        db.createUser(user);
        assertThrows(ResponseException.class, () -> db.createUser(user));
    }

    //create user and get their data.
    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserDataPositive(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO db = (UserDAO) getDataAccess(dbClass)[1];
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));

        UserData user = db.getUserData("brian");
        assertNotNull(user);
        assertEquals("brian", user.username());
    }

    //get a user's data that we didn't create
    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUserDataNegative(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO db = (UserDAO) getDataAccess(dbClass)[1];

        UserData user = db.getUserData("nonExistent");
        assertNull(user);
    }

    //make sure that clear works
    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void clearUserTest(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO db = (UserDAO) getDataAccess(dbClass)[1];
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));
        db.createUser(new UserData("jeff", "password123", "jeff@gmail.com"));

        db.clear();

        assertNull(db.getUserData("brian"));
    }

    //test getting all users. add 2 make sure there really is 2.
    @ParameterizedTest
    @ValueSource(classes = {SQLUserDAO.class, MemoryUserDAO.class})
    void getUsersPositive(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO db = (UserDAO) getDataAccess(dbClass)[1];
        db.createUser(new UserData("brian", "password", "brian@gmail.com"));
        db.createUser(new UserData("jeff", "password123", "jeff@gmail.com"));

        HashMap<Integer, UserData> users = db.getUsers();
        assertEquals(2, users.size());
    }
}