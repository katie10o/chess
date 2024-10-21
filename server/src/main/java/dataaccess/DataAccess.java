package dataaccess;

import chess.ChessGame;
import model.AuthTokenData;
import model.UserData;

import java.util.Collection;


public interface DataAccess {
    void addUser(UserData usrData);
    boolean getUserData(String username);
    void addAuthToken(AuthTokenData tokenData);
    void clearDB();
//    void clearDB();
//    void deleteAuthToken();
//    Collection<ChessGame> listGames();
//    ChessGame createGame();

}
