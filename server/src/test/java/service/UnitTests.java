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
}