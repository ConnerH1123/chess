package server;

import org.eclipse.jetty.websocket.api.Session;

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

//    public void broadcast(Session excludeSession, Notification notification) throws IOException {
//        String msg = notification.toString();
//        for (Session c : connections.values()) {
//            if (c.isOpen()) {
//                if (!c.equals(excludeSession)) {
//                    c.getRemote().sendString(msg);
//                }
//            }
//        }
//    }
}
