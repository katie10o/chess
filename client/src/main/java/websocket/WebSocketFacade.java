package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import draw.DrawBoard;
import responseex.ResponseException;
import websocket.commands.UserCommandMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
//            will always be a message from ServerMessage
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
                        ChessGame game = serverMessage.getGame();
                        ChessGame.TeamColor teamColor = serverMessage.getTeamColor();
                        String board = draw(game.getBoard().toString(), teamColor);
                        serverMessage.addMessage(board);
                    } else if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){

                    }


                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    private String draw(String board, ChessGame.TeamColor teamColor){
        DrawBoard boardObject = new DrawBoard(teamColor, board, false, new ArrayList<>() );
        return boardObject.getDrawnBoard();
    }

    public void joinGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observeGame(String authToken, Integer gameID) throws ResponseException{
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void makeMove(String authToken, Integer gameID, UserCommandMove move) throws ResponseException {
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            String message = new Gson().toJson(connect);
            String moveMessage = new Gson().toJson(move);
            this.session.getBasicRemote().sendText(message + moveMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var connect = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
