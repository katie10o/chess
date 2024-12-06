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
    //loadGame info
    ChessGame game;
    String move;
    String username;
    ChessGame.TeamColor teamColor;
    String board;


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

    public void addGame(ChessGame game){this.game = game;}
    public ChessGame getGame(){return game;}

    public void addMove(String move){this.move = move;}
    public String getMove(){return move;}

    public void addUser(String user){this.username = user;}
    public String getUser(){return username;}

    public void addTeamColor(ChessGame.TeamColor teamColor){this.teamColor = teamColor;}
    public ChessGame.TeamColor getTeamColor(){return teamColor;}

    public void addBoard(String board){this.board = board;}
    public String getBoard(){return board;}


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
