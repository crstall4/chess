package service;

import com.mysql.cj.log.Log;
import dataaccess.*;
import exception.ResponseException;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passoff.model.TestCreateRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnitTests {
    private static final Logger log = LoggerFactory.getLogger(UnitTests.class);
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();


    LoginService loginService = new LoginService(userDAO, authDAO);
    RegisterService registerService = new RegisterService(userDAO,loginService);
    LogoutService logoutService = new LogoutService(authDAO);
    ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    UserService testService = new UserService(userDAO, authDAO, gameDAO);
    CreateGameService createGameService = new CreateGameService(gameDAO,authDAO);

    @BeforeEach
    void clear() throws ResponseException {
        clearService.clearAllDatabases();
    }

    //test clear, by registering someone, clearing the DBs, and then seeing if the users list is 1 or 0
    @Test
    void testClear() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        registerService.registerUser(user);
        clearService.clearAllDatabases();

        var users = testService.getUsers();

        assertEquals(0, users.size());
        assertFalse(users.containsKey(user.username().hashCode()));
    }

    //try to register like normal. make sure that users.size() = 1.
    @Test
    void registerGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        registerService.registerUser(user);

        var users = testService.getUsers();

        assertEquals(1, users.size());
        assertTrue(users.containsKey(user.username().hashCode()));
    }

    //try to register with null username
    @Test
    void registerBad() {
        var user = new UserData(null, "password123", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> registerService.registerUser(user));
        assertEquals(400, exception.getStatusCode());
    }

    //login like normal. see if brian is the username in auth, and that a proper auth token was returned
    @Test
    void loginGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        registerService.registerUser(user);
        loginService.loginUser(user);

        var auth = loginService.loginUser(user);

        assertEquals("brian", auth.username());
        assertNotNull(auth.authToken());
    }

    //login without registering
    @Test
    void loginBad() {
        var user = new UserData("brian", "password1234", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> loginService.loginUser(user));
        assertEquals(401, exception.getStatusCode());
    }

    //register and log out without errors
    @Test
    void logoutGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        logoutService.logoutUser(auth.authToken());
    }

    //tries to log out 2 times
    @Test
    void logoutBad() throws ResponseException{
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        logoutService.logoutUser(auth.authToken());
        ResponseException exception = assertThrows(ResponseException.class, () -> logoutService.logoutUser(auth.authToken()));
        assertEquals(401, exception.getStatusCode());
    }

    //register and use auth token to create game
    @Test
    void createGameGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        GameData game = new GameData(4,null, null,"this is the name", null);

        createGameService.createGame(auth.authToken(),game);
    }

    //tries to create game with a bad token...
    @Test
    void createGameBad() throws ResponseException{
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        GameData game = new GameData(4,null, null,null, null);
        ResponseException exception = assertThrows(ResponseException.class, () -> createGameService.createGame("fake auth token",game));
        assertEquals(401, exception.getStatusCode());
    }

}