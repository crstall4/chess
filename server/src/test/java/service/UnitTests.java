package service;

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

    @BeforeEach
    void clear() throws ResponseException {
        service.deleteAllDatabases();
    }

    @Test
    void testClear() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        service.createUser(user);
        service.deleteAllDatabases();

        var users = service.getUsers();

        assertEquals(0, users.size());
        assertFalse(users.containsKey(user.username().hashCode()));
    }

    @Test
    void registerGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        service.createUser(user);

        var users = service.getUsers();

        assertEquals(1, users.size());
        assertTrue(users.containsKey(user.username().hashCode()));
    }

    @Test
    void registerBad() {
        var user = new UserData(null, "password123", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> service.createUser(user));
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void loginGood() throws ResponseException {
        var user = new UserData("brian", "password123", "brian@gmail.com");
        service.createUser(user);

        var auth = service.loginUser(user);

        assertEquals("brian", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void loginBad() {
        var user = new UserData("brian", "password1234", "brian@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> service.loginUser(user));
        assertEquals(401, exception.getStatusCode());
    }
}