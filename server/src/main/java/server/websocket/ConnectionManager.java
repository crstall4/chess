package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(int gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        Set<Session> sessions = connections.getOrDefault(gameID, Set.of());
        for (Session session : sessions) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.getRemote().sendString(json);
            }
        }
    }

    public void sendToSession(Session session, ServerMessage message) throws IOException {
        String json = new Gson().toJson(message);
        if (session.isOpen()) {
            session.getRemote().sendString(json);
        }
    }
}
