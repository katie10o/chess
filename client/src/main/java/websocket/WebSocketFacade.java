package websocket;

import com.google.gson.Gson;
import responseex.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinGame(String user, String teamColor, String authToken, Integer gameID) throws ResponseException {
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            connect.addUser(user);
            connect.addTeamColor(teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(String user, String authToken, Integer gameID) throws ResponseException{
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            connect.addUser(user);
            connect.setObserver();
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }



    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
