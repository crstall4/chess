package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import exception.ResponseException;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    void registerSuccess() throws ResponseException {
        var auth = facade.register("testuser", "password", "test@gmail.com");
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertEquals("testuser", auth.username());
    }

    @Test
    void registerFails() throws ResponseException {
        facade.register("testuser", "password", "test@gmail.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.register("testuser", "password", "test@gmail.com"));
    }

    @Test
    void loginSuccess() throws ResponseException {
        var registerAuth = facade.register("testuser", "password", "test@gmail.com");
        facade.logout(registerAuth.authToken());
        var loginAuth = facade.login("testuser", "password");

        Assertions.assertNotNull(loginAuth.authToken());
        Assertions.assertEquals("testuser", loginAuth.username());
    }

    @Test
    void loginFails() throws ResponseException {
        var registerAuth = facade.register("testuser", "password", "test@gmail.com");
        facade.logout(registerAuth.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.login("testuser", "wrong_password"));

    }

}
