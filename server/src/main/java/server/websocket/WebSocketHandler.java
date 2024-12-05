package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> {
                if (action.getObserver()){
                    observeGame(action.getUser(), session);
                }  else {
                    joinGame(action.getUser(), action.getTeamColor(), session);
                }
            }
            case MAKE_MOVE -> makeMove(action.getUser(), action.getTeamColor());
            case LEAVE -> leaveGame(action.getUser(),  session);
            case RESIGN -> resignGame(action.getUser(), action.getTeamColor());
        }
    }
    private void resignGame(String visitorName, String teamColor) throws IOException {
        var message = String.format("%s (%s player) resigned", visitorName, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }

    private void makeMove(String visitorName, String teamColor) throws IOException {

        var message = String.format("%s (%s player) made a move", visitorName, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }

    private void leaveGame(String visitorName, Session session) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }


    private void observeGame(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s joined the game as an observer", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }

    private void joinGame(String visitorName, String teamColor, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s joined the game as %s player", visitorName, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }

}
