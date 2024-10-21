package dataaccess;

import model.AuthTokenData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;


public interface DataAccess {
    void addUser(UserData usrData);
    boolean checkUserName(String username);
    void addAuthToken(AuthTokenData tokenData);
    void clearDB();
    String getUserPassword(String userName) throws DataAccessException;
    void clearAuthToken(String authToken);
    boolean getAuthToken(String authToken);
    int addGame(GameData gameData);
    void editGame(GameData gameData);
    GameData getGameData(GameData gameData);
    boolean checkGameID(GameData gameData);
    String getUserName(String authToken);
    HashMap<String, Collection<GameData>> listGames();

}
