package server;

import exception.ResponseException;

public class ServerMain {
    public static void main(String[] args) throws ResponseException {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}