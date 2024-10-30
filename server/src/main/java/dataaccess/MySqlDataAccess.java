package dataaccess;

import model.AuthTokenData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MySqlDataAccess implements DataAccess{
    @Override
    public void addUser(UserData usrData) throws DataAccessException {

    }

    @Override
    public boolean checkUserName(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void addAuthToken(AuthTokenData tokenData) throws DataAccessException {

    }

    @Override
    public void clearDB() throws DataAccessException {

    }

    @Override
    public String getUserPassword(String userName) throws DataAccessException {
        return "";
    }

    @Override
    public void clearAuthToken(String authToken) throws DataAccessException {

    }

    @Override
    public boolean getAuthToken(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public int addGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    @Override
    public void editGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGameData(GameData gameData) throws DataAccessException {
        return null;
    }

    @Override
    public boolean checkGameID(GameData gameData) throws DataAccessException {
        return false;
    }

    @Override
    public String getUserName(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() throws DataAccessException {
        return null;
    }
}
