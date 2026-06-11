package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        ArrayList<Session> connection = connections.get(gameID);
        if (connection == null) {
            ArrayList<Session> newConnection = new ArrayList<>();
            newConnection.add(session);
            connections.put(gameID, newConnection);
        }
        else {
            connection.add(session);
        }
    }

    public void remove(int gameID, Session session) {
        ArrayList<Session> connection = connections.get(gameID);
        if (connection != null) {
            connection.remove(session);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        ArrayList<Session> connection = connections.get(gameID);
        if (connection != null) {
            for (Session c : connection) {
                if (c.isOpen() && !c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void notifyClient(Session clientSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        if (clientSession != null && clientSession.isOpen()) {
            clientSession.getRemote().sendString(msg);
        }
    }
}
