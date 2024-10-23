package dataAccess;

import model.AuthTokenData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;


public interface DataAccess {
    void addUser(UserData usrData) throws DataAccessException;
    boolean checkUserName(String username) throws DataAccessException;
    void addAuthToken(AuthTokenData tokenData) throws DataAccessException;
    void clearDB() throws DataAccessException;
    String getUserPassword(String userName) throws DataAccessException;
    void clearAuthToken(String authToken) throws DataAccessException;
    boolean getAuthToken(String authToken) throws DataAccessException;
    int addGame(GameData gameData) throws DataAccessException;
    void editGame(GameData gameData) throws DataAccessException;
    GameData getGameData(GameData gameData) throws DataAccessException;
    boolean checkGameID(GameData gameData) throws DataAccessException;
    String getUserName(String authToken) throws DataAccessException;
    HashMap<String, Collection<GameData>> listGames() throws DataAccessException;

}
