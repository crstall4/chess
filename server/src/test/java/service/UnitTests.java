package service;

import com.mysql.cj.log.Log;
import dataaccess.*;
import exception.ResponseException;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnitTests {
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    final UserService service = new UserService(userDAO, authDAO, gameDAO);
    final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    final LoginService loginService = new LoginService(userDAO, authDAO);
    final RegisterService registerService = new RegisterService(userDAO, loginService);

    @BeforeEach
    void clear() throws ResponseException {
        clearService.clearAllDatabases();
    }

    @Test
    void testClear() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        registerService.registerUser(user);
        clearService.clearAllDatabases();

        var users = service.getUsers();

        assertEquals(0, users.size());
        assertFalse(users.containsKey(user.username().hashCode()));
    }

    @Test
    void registerGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        registerService.registerUser(user);

        var users = service.getUsers();

        assertEquals(1, users.size());
        assertTrue(users.containsKey(user.username().hashCode()));
    }

    @Test
    void registerBad() {
        var user = new UserData(null, "password123", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> registerService.registerUser(user));
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void loginGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        loginService.loginUser(user);

        var auth = loginService.loginUser(user);

        assertEquals("brian", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void loginBad() {
        var user = new UserData("brian", "password1234", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> loginService.loginUser(user));
        assertEquals(401, exception.getStatusCode());
    }
}