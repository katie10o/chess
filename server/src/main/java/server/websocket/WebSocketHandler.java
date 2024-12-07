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
    private final Service service;

    public WebSocketHandler(ServerHandler serverHandler){
        this.service = serverHandler.getService();
    }

//    will always be a message from UserGameCommand
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        UserCommandMove move = new Gson().fromJson(message, UserCommandMove.class);

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
                connections.singleNotification(session,  errorMessage, gameID);
                return;
            }
            if (teamColor == null) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "observers cannot resign game");
                connections.singleNotification(session,  errorMessage, gameID);
                return;
            }
            gameData.gameObject().resignGame();
            service.updateGame(gameData, authToken);

            String winner = teamColor.equals(ChessGame.TeamColor.WHITE) ? "BLACK" : "WHITE";
            var message = String.format("%s (%s player) resigned. %s wins!", visitorName, teamColor, winner);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, true, gameID);
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
            if (!moveChecks(gameData, visitorName, gameID, session, teamColor, move)){
                return;
            }
            afterMove(gameData, gameID, visitorName);

            try{
                gameData.gameObject().makeMove(new ChessMove(move.getStartPosition(), move.getEndPosition(), move.getPieceType()));
                service.updateGame(gameData, authToken);
            } catch (InvalidMoveException ex){
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: move cannot be made");
                connections.singleNotification(session, errorMessage, gameID);
                return;
            }

            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.addGame(gameData.gameObject());
            connections.broadcastGame(loadGame, gameID);

            var message = String.format("\n%s (%s player) moved", visitorName, teamColor);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, false, gameID);

//            GameData updatedGameData = getGameData(authToken, gameID);
//            afterMove(updatedGameData, gameID, visitorName);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: move cannot be made" );
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

            connections.remove(visitorName, gameID);
            var message = String.format("%s left the game", visitorName);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, false, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session,  errorMessage, gameID);
        }
    }

    private void connect(Session session, String authToken, Integer gameID) throws IOException {
        try {
            String visitorName = getUserName(authToken);
            GameData gameData = getGameData(authToken, gameID);
            ChessGame.TeamColor teamColor = getTeamColor(gameData, visitorName);
            if (teamColor == null) {
                connections.add(session, gameID, ChessGame.TeamColor.WHITE, visitorName);
                observeGame(session, visitorName, gameData.gameObject(), gameID);
            } else {
                connections.add(session, gameID, teamColor, visitorName);
                joinGame(session, visitorName, teamColor, gameData.gameObject(), gameID);
            }

        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session,  errorMessage, gameID);
        }
    }

    private void observeGame(Session session, String visitorName, ChessGame game, Integer gameID) throws IOException {
        try {
            loadGame(session, visitorName, ChessGame.TeamColor.WHITE, game, gameID);

            var message = String.format("%s joined the game as an observer", visitorName);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, false, gameID);
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
            connections.boradcastNotification(visitorName, notification, false, gameID);
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
            connections.singleNotification(session,  loadGame, gameID);
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + ex);
            connections.singleNotification(session, errorMessage, gameID);
        }
    }
    private void afterMove(GameData gameData, Integer gameID, String visitorName) throws IOException {

        if (checkMate(gameData)){
            String message = "king in checkmate, game over";
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, true, gameID);
        }
        if (checkStale(gameData)){
            String message = "king in stalemate, game tied";
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, true, gameID);
        }
        if (check(gameData)){
            String message = "king in check";
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            connections.boradcastNotification(visitorName, notification, true, gameID);
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
        return game.gameObject().isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.gameObject().isInCheckmate(ChessGame.TeamColor.BLACK);
    }
    private boolean checkStale(GameData game){
        return game.gameObject().isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.gameObject().isInStalemate(ChessGame.TeamColor.BLACK);
    }
    private boolean check(GameData game){
        return game.gameObject().isInCheck(ChessGame.TeamColor.WHITE) ||
                game.gameObject().isInCheck(ChessGame.TeamColor.BLACK);
    }
    private boolean checkResign(GameData game){
        return game.gameObject().getResigned();
    }
    private boolean moveChecks(GameData gameData, String visitorName, Integer gameID,
                               Session session, ChessGame.TeamColor teamColor, UserCommandMove move ) throws IOException {
        try {
            if (!gameData.whiteUsername().equals(visitorName) && !gameData.blackUsername().equals(visitorName)) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "observers cannot make a move");
                connections.singleNotification(session,  errorMessage, gameID);
                return false;
            }
            if (!gameData.gameObject().getTeamTurn().equals(teamColor)) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "Not your turn to go");
                connections.singleNotification(session, errorMessage, gameID);
                return false;
            }
            if (!teamColor.equals(gameData.gameObject().getBoard().getPiece(move.getStartPosition()).getTeamColor())) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "cannot move piece that does not belong to you");
                connections.singleNotification(session, errorMessage, gameID);
                return false;
            }
            if (checkMate(gameData)) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is over, king in checkmate");
                connections.singleNotification(session, errorMessage, gameID);
                return false;
            }
            if (checkResign(gameData)) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is over, previous player resigned");
                connections.singleNotification(session,  errorMessage, gameID);
                return false;
            }
            if (checkStale(gameData)) {
                var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                errorMessage.addErrorMessage("errorMessage: " + "game is tied, king in stalemate");
                connections.singleNotification(session, errorMessage, gameID);
                return false;
            }

            return true;
        } catch (Exception ex){
            var errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            errorMessage.addErrorMessage("errorMessage: " + "unexpected error");
            connections.singleNotification(session, errorMessage, gameID);
            return false;
        }
    }
}
