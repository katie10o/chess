package websocket.messages;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    //notification update or error message
    String message;
    String errorMessage;
    //loadGame info
    ChessGame game;
    String username;
    ChessGame.TeamColor teamColor;


    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public void addMessage(String message){this.message = message;}
    public String getMessage(){return message;}

    public void addErrorMessage(String message){this.errorMessage = message;}
    public String getErrorMessage(){return errorMessage;}

    public void addGame(ChessGame game){this.game = game;}
    public ChessGame getGame(){return game;}


    public void addUser(String user){this.username = user;}
    public String getUser(){return username;}

    public void addTeamColor(ChessGame.TeamColor teamColor){this.teamColor = teamColor;}
    public ChessGame.TeamColor getTeamColor(){return teamColor;}



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
