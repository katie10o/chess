package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class ConnectionManager {
    public final HashMap<Integer, ArrayList<Connection>> connections = new HashMap<>();

    public void add(Session rootSession, Integer gameID, ChessGame.TeamColor teamColor, String visitorName) {

        var connection = new Connection(rootSession, gameID, teamColor, visitorName);
        if (!connections.containsKey(gameID)){
            connections.put(gameID, new ArrayList<>());
        }
        connections.get(gameID).add(connection);
    }

    public void remove(String visitorName, Integer gameID) {
        List<Connection> connectionList = connections.get(gameID);
        System.out.println(connectionList.size());
        connectionList.removeIf(con -> con.visitorName.equals(visitorName));
        List<Connection> connectionList2 = connections.get(gameID);
        System.out.println(connectionList2.size());

    }

    public void boradcastNotification(String visitorName, ServerMessage notification, boolean toAll, Integer gameID) throws IOException {
        for (Connection c : connections.get(gameID)) {
            boolean open = c.session.isOpen();
            if (c.session.isOpen()) {
                if (toAll){
                    c.send(new Gson().toJson(notification));
                }
                else if (!c.visitorName.equals(visitorName)) {
                    c.send(new Gson().toJson(notification));
                }
            }
        }
    }

    public void singleNotification(Session session, ServerMessage message, Integer gameID) throws IOException {
        if (session.isOpen()){
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    public void broadcastGame(ServerMessage message, Integer gameID) throws IOException {
        for (Connection c : connections.get(gameID)) {
            boolean open = c.session.isOpen();
            if (c.session.isOpen()) {
                var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                loadGame.addGame(message.getGame());
                loadGame.addTeamColor(c.teamColor);

                c.send(new Gson().toJson(loadGame));
            }
        }
    }
}
