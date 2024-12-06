package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();

    public void add(Session rootSession, Integer gameID) {
        var connection = new Connection(rootSession);
        if (connections.containsKey(gameID)){
            connections.get(gameID).add(connection);
        }
        else {
            connections.put(gameID, new ArrayList<>());
            connections.get(gameID).add(connection);
        }
    }

    public void remove(Session rootSession, Integer gameID) {
        connections.get(gameID).removeIf(con -> con.session.equals(rootSession));
    }

    public void boradcastNotification(Session rootSession, ServerMessage notification, boolean toAll, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c : connections.get(gameID)) {
            boolean open = c.session.isOpen();
            if (c.session.isOpen()) {
                if (toAll){
                    c.send(new Gson().toJson(notification));
                }
                else if (!c.session.equals(rootSession)) {
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.get(gameID).removeIf(con -> con.session.equals(c.session));
        }
    }

    public void singleNotification(Session rootSession, ServerMessage message, Integer gameID) throws IOException {
        for (Connection con : connections.get(gameID)){
            if (con.getSession().equals(rootSession)){
                if (con.session.isOpen()){
                    con.send(new Gson().toJson(message));
                }
            }
        }
    }

    public void broadcastGame(ChessGame.TeamColor teamColor, ServerMessage message, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.getSession().isOpen()) {
                var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                loadGame.addGame(message.getGame());
                loadGame.addTeamColor(teamColor);

                c.send(new Gson().toJson(loadGame));
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.get(gameID).removeIf(con -> con.session.equals(c.session));
        }
    }
}
