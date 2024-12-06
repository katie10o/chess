package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final HashMap<Session, Connection> connections = new HashMap<>();

    public void add(Session session) {
        var connection = new Connection(session);
        connections.put(session, connection);
    }

    public void remove(Session session) {

        connections.remove(session);
    }

    public void boradcastNotification(Session session, ServerMessage notification, boolean toAll) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (toAll){
                    c.send(new Gson().toJson(notification));
                }
                else if (!c.session.equals(session)) {
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.session);
        }
    }

    public void singleNotification(Session session, ServerMessage message) throws IOException {
        Connection con = connections.get(session);
        if (con.session.isOpen()){
            con.send(new Gson().toJson(message));
        }
    }

    public void broadcastGame(ChessGame.TeamColor teamColor, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                loadGame.addGame(message.getGame());
                loadGame.addTeamColor(teamColor);

                c.send(new Gson().toJson(loadGame));
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.session);
        }
    }
}
