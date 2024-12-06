package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;

public class Connection {
    public Session session;

    public Connection(Session session) {
        this.session = session;
    }

    public Session getSession(){
        return session;
    }
    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(session);
    }
}
