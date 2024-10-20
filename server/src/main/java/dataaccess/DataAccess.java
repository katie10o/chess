package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.util.Collection;


public interface DataAccess {
    void addUser(String username, String password, String email);
    boolean getUserData(String username);
    void addAuthToken(String username, String token);
//    void clearDB();
//    void deleteAuthToken();
//    Collection<ChessGame> listGames();
//    ChessGame createGame();

}
