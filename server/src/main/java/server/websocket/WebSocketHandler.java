package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import responseex.ResponseException;
import server.ServerHandler;
import service.Service;
import websocket.commands.UserCommandMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final ServerHandler serverHandler;
    private final Service service;

    public WebSocketHandler(ServerHandler serverHandler){
        this.serverHandler = serverHandler;
        this.service = serverHandler.getService();
    }

//    will always be a message from UserGameCommand
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        UserCommandMove move = new Gson().fromJson(message, UserCommandMove.class);

//        UserCommandMove move = new Gson().fromJson(message, UserCommandMove.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(session, action.getAuthToken(), action.getGameID());
            case MAKE_MOVE -> makeMove(session, action.getAuthToken(), action.getGameID(), move);
            case LEAVE -> leaveGame(session, action.getAuthToken(), action.getGameID());
            case RESIGN -> resignGame(session, action.getAuthToken(), action.getGameID());
        }
    }
    private void resignGame(Session session, String authToken, Integer gameID) throws IOException {
        try {
            String visitorName = getUserName(authToken);
            ChessGame.TeamColor teamColor = getTeamColor(getGameData(authToken, gameID), visitorName);
            GameData gameData = getGameData(authToken, gameID);


            if (checkResign(gameData)){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is over, previous player resigned");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }
            gameData.gameObject().resignGame();
            service.updateGame(gameData, authToken);

            String winner = teamColor.equals(ChessGame.TeamColor.WHITE) ? "BLACK" : "WHITE";
            var message = String.format("%s (%s player) resigned. %s wins!", visitorName, teamColor, winner);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(session, notification, true, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void makeMove(Session session, String authToken, Integer gameID, UserCommandMove move) throws IOException {
        try {
            move.setPostions();
            String visitorName = getUserName(authToken);
            GameData gameData = getGameData(authToken, gameID);
            ChessGame.TeamColor teamColor = getTeamColor(gameData, visitorName);
            if (!gameData.whiteUsername().equals(visitorName) && !gameData.blackUsername().equals(visitorName)){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "observers cannot make a move");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }
            if (!teamColor.equals(gameData.gameObject().getBoard().getPiece(move.getStartPosition()).getTeamColor())){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "cannot move piece that does not belong to you");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }
            if (checkMate(gameData)){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is over, king in checkmate");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }
            if (checkResign(gameData)){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is over, previous player resigned");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }

            try{
                gameData.gameObject().makeMove(new ChessMove(move.getStartPosition(), move.getEndPosition(), null));
                service.updateGame(gameData, authToken);
            } catch (InvalidMoveException ex){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + ex);
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }

            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.addGame(gameData.gameObject());
            connections.broadcastGame(teamColor, loadGame, gameID);

            var message = String.format("\n%s (%s player) moved", visitorName, teamColor);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(session, notification, false, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void leaveGame(Session session, String authToken, Integer gameID) throws IOException {
        try {
            String visitorName = getUserName(authToken);
            ChessGame.TeamColor teamColor = getTeamColor(getGameData(authToken, gameID), visitorName);

            GameData gameData = getGameData(authToken, gameID);
            GameData newGameData;
            if (teamColor != null ){
                if (teamColor.equals(ChessGame.TeamColor.WHITE)){
                    newGameData  = new GameData(gameData.gameID(), null, gameData.blackUsername(),
                            gameData.gameName(), gameData.gameObject(), gameData.playerColor(), gameData.authToken());
                } else {
                    newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null,
                            gameData.gameName(), gameData.gameObject(), gameData.playerColor(), gameData.authToken());
                }
                service.leaveGame(newGameData, authToken);
            }

            connections.remove(session, gameID);
            var message = String.format("%s left the game", visitorName);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(session, notification, false, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void connect(Session session, String authToken, Integer gameID) throws IOException {
        try {
            connections.add(session, gameID);
            String visitorName = getUserName(authToken);
            GameData gameData = getGameData(authToken, gameID);
            ChessGame.TeamColor teamColor = getTeamColor(gameData, visitorName);
            if (teamColor == null) {
                observeGame(session, visitorName, ChessGame.TeamColor.WHITE, gameData.gameObject(), gameID);
            } else {
                joinGame(session, visitorName, teamColor, gameData.gameObject(), gameID);
            }
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void observeGame(Session session, String visitorName, ChessGame.TeamColor teamColor, ChessGame game, Integer gameID) throws IOException {
        try {
            loadGame(session, visitorName, teamColor, game, gameID);

            var message = String.format("%s joined the game as an observer", visitorName);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(session, notification, false, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void joinGame(Session session, String visitorName, ChessGame.TeamColor teamColor, ChessGame game, Integer gameID) throws IOException {
        try {
            loadGame(session, visitorName, teamColor, game, gameID);

            var message = String.format("%s joined the game as %s player", visitorName, teamColor);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(session, notification, false, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private void loadGame(Session session, String visitorName, ChessGame.TeamColor teamColor, ChessGame game, Integer gameID) throws IOException {
        try {
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.addUser(visitorName);
            loadGame.addTeamColor(teamColor);
            loadGame.addGame(game);
            connections.singleNotification(session, loadGame, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }

    private String getUserName(String authToken) throws ResponseException, DataAccessException {
        return service.getUser(authToken);
    }
    private GameData getGameData(String authToken, Integer gameID) throws ResponseException, DataAccessException {
        return service.getGame(authToken, gameID);
    }
    private ChessGame.TeamColor getTeamColor(GameData data, String userName) {
        if (data.blackUsername() != null && data.blackUsername().equals(userName)) {
            return ChessGame.TeamColor.BLACK;
        } else if (data.whiteUsername() != null && data.whiteUsername().equals(userName)) {
            return ChessGame.TeamColor.WHITE;
        }
        return null;
    }
    private boolean checkMate(GameData game){
        if (game.gameObject().isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.gameObject().isInCheckmate(ChessGame.TeamColor.BLACK) ){
            return true;
        }
        return false;
    }
    private boolean checkResign(GameData game){
        return game.gameObject().getResigned();
    }
}
