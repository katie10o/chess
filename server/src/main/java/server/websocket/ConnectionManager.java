package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class ConnectionManager {
    public final HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();

    public void add(Session rootSession, Integer gameID) {
        var connection = new Connection(rootSession, gameID);
        if (!connections.containsKey(gameID)){
            connections.put(gameID, new ArrayList<>());
        }
        connections.get(gameID).add(connection);
    }

    public void remove(Session rootSession, Integer gameID) {
        List<Connection> connectionList = connections.get(gameID);
        connectionList.removeIf(con -> con.session.equals(rootSession));
    }

    public void boradcastNotification(Session rootSession, ServerMessage notification, boolean toAll, Integer gameID) throws IOException {
        for (Connection c : connections.get(gameID)) {
            boolean open = c.session.isOpen();
            if (c.session.isOpen()) {
                if (toAll){
                    c.send(new Gson().toJson(notification));
                }
                else if (!c.session.equals(rootSession)) {
                    c.send(new Gson().toJson(notification));
                }
            }
        }
    }

    public void singleNotification(Session rootSession, ServerMessage message, Integer gameID) throws IOException {
        for (Connection con : connections.get(gameID)){
            if (con.session.equals(rootSession)){
                boolean open = con.session.isOpen();
                if (con.session.isOpen()){
                    con.send(new Gson().toJson(message));
                }
            }
        }
    }

    public void broadcastGame(ChessGame.TeamColor teamColor, ServerMessage message, Integer gameID) throws IOException {
        for (Connection c : connections.get(gameID)) {
            boolean open = c.session.isOpen();
            if (c.session.isOpen()) {
                var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                loadGame.addGame(message.getGame());
                loadGame.addTeamColor(teamColor);

                c.send(new Gson().toJson(loadGame));
            }
        }
    }
}
