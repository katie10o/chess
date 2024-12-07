package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Objects;

public class Connection {
    public Session session;
    public Integer gameID;
    public ChessGame.TeamColor teamColor;
    public String visitorName;

    public Connection(Session session, Integer gameID, ChessGame.TeamColor teamColor, String visitorName) {
        this.session = session;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.visitorName = visitorName;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Connection that = (Connection) o;
        return Objects.equals(visitorName, that.visitorName) && Objects.equals(gameID, that.gameID) && Objects.equals(teamColor, that.teamColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, gameID);
    }
}
