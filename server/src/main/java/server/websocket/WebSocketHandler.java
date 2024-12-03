package server.websocket;

import com.google.gson.Gson;
import model.UserGameCommandData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import responseex.ResponseException;
import server.Server;
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
            case CONNECT -> joinGame(action.getUser(), action.getTeamColor(), session);
//            case MAKE_MOVE -> makeMove();
//            case LEAVE -> leaveGame();
//            case RESIGN -> resignGame();
        }
    }
    private void joinGame(String visitorName, String teamColor, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s joined the game as %s player", visitorName, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(visitorName, notification);
    }

}
