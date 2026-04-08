package server;

import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.a.ReaderValueEncoder;
import dataaccess.*;
import exception.ResponseException;
import handler.*;
import io.javalin.*;
import server.websocket.WebSocketHandler;
import service.*;
import service.*;


public class Server {
    private final Javalin javalin;
    public static boolean useSQL = true;

    public static Object[] createDAOs() {
        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            if(useSQL) {
                userDAO = new SQLUserDAO();
                gameDAO = new SQLGameDAO();
                authDAO = new SQLAuthDAO();
            }else{
                userDAO = new MemoryUserDAO();
                gameDAO = new MemoryGameDAO();
                authDAO = new MemoryAuthDAO();
            }
        } catch(Exception ignored) {
            userDAO = null;
            gameDAO = null;
            authDAO = null;
        }
        return new Object[]{userDAO, authDAO, gameDAO};
    }

    public Server() {
        Object[] daos = createDAOs();
        UserDAO userDAO = (UserDAO) daos[0];
        AuthDAO authDAO = (AuthDAO) daos[1];
        GameDAO gameDAO = (GameDAO) daos[2];
        LoginService loginService = new LoginService(userDAO, authDAO);
        RegisterService registerService = new RegisterService(userDAO,loginService);
        LogoutService logoutService = new LogoutService(authDAO);
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        UserService userService = new UserService(userDAO, authDAO, gameDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO,authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO,authDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO,authDAO);

        LoginHandler loginHandler = new LoginHandler(loginService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);

        WebSocketHandler webSocketHandler = new WebSocketHandler(gameDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", registerHandler::handle)
                .delete("/db", clearHandler::handle)
                .post("/session", loginHandler::handle)
                .delete("/session", logoutHandler::handle)
                .post("/game", createGameHandler::handle)
                .get("/game", listGamesHandler::handle)
                .put("/game", joinGameHandler::handle)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
