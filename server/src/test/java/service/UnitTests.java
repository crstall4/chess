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
    private static final Logger LOG = LoggerFactory.getLogger(UnitTests.class);
    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;

    UserService testService;

    LoginService loginService;
    RegisterService registerService;
    LogoutService logoutService;
    ClearService clearService;
    CreateGameService createGameService;
    ListGamesService listGamesService;
    JoinGameService joinGameService;

    UnitTests() throws ResponseException {
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        authDAO = new SQLAuthDAO();

        testService = new UserService(userDAO, authDAO, gameDAO);
        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, loginService);
        logoutService = new LogoutService(authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        createGameService = new CreateGameService(gameDAO, authDAO);
        listGamesService = new ListGamesService(gameDAO, authDAO);
        joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    @BeforeEach
    void clear() throws ResponseException {
        clearService.clearAllDatabases();
        var users = testService.getUsers();
        assertEquals(0, users.size());
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

    //register and log out, AND THEN try to use auth token (list games) that you already logged out of.
    @Test
    void logoutGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        logoutService.logoutUser(auth.authToken());
        ResponseException exception = assertThrows(ResponseException.class, () -> listGamesService.listGames(auth.authToken()));
        assertEquals(401, exception.getStatusCode());
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

    //create 2 games, make sure the amount of games is actually 2.
    @Test
    void createGameGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        GameData game = new GameData(4,null, null,"this is the name", null);

        createGameService.createGame(auth.authToken(),game);
        createGameService.createGame(auth.authToken(),new GameData(5,null, null,"ASDF", null));
        var games = listGamesService.listGames(auth.authToken());
        assertEquals(2, games.size());
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

    //creates a game, then lists games and sees if the size of them increased by one.
    @Test
    void listGamesGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        GameData game = new GameData(4,null, null,"this is the name", null);
        createGameService.createGame(auth.authToken(), game);
        var games = listGamesService.listGames(auth.authToken());
        assertEquals(1, games.size());
    }

    //tries to list games with a bad token
    @Test
    void listGamesBad() throws ResponseException{
        ResponseException exception = assertThrows(ResponseException.class, () -> listGamesService.listGames("fake token"));
        assertEquals(401, exception.getStatusCode());
    }

    //join game like normal
    @Test
    void joinGameGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        var auth = registerService.registerUser(user);
        GameData game = new GameData(1,null, null,"this is the name", null);
        createGameService.createGame(auth.authToken(), game);
        assertDoesNotThrow(() -> joinGameService.joinGame(auth.authToken(), "WHITE", 1));
        
    }

    //has 2 players join 1 game, but as the same color.
    @Test
    void joinGameBad() throws ResponseException{
        var user1 = new UserData("brian", "password123", "brian@gmail.com");
        var auth1 = registerService.registerUser(user1);
        var user2 = new UserData("charles", "passwasdaord123", "charles@gmail.com");
        var auth2 = registerService.registerUser(user2);
        GameData game = new GameData(1,null, null,"this is the name", null);
        createGameService.createGame(auth1.authToken(), game);
        joinGameService.joinGame(auth1.authToken(), "WHITE", 1);
        ResponseException exception = assertThrows(ResponseException.class, () -> joinGameService.joinGame(auth2.authToken(), "WHITE", 1));
        assertEquals(403, exception.getStatusCode());
    }

}