package dataaccess;

import model.AuthTokenData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    HashMap<String, UserData> userInfo = new HashMap<>();
    HashMap<String, String> gameInfo = new HashMap<>();
    HashMap<String, AuthTokenData> authTokenInfo = new HashMap<>();

    @Override
    public void addUser(UserData usrData) {
        userInfo.put(usrData.username(), usrData);
    }

    public boolean getUserName(String username){
        return userInfo.containsKey(username);
    }
    public void addAuthToken(AuthTokenData tokenData){
        authTokenInfo.put(tokenData.authToken(), tokenData);
    }

    public boolean getAuthToken(String authToken){
        return authTokenInfo.containsKey(authToken);
    }

    @Override
    public void clearDB() {
        userInfo.clear();
        gameInfo.clear();
        authTokenInfo.clear();
    }

    @Override
    public String getUserPassword(String username) {
        return userInfo.get(username).password();

    }

    @Override
    public void clearAuthToken(String authToken) {
        authTokenInfo.remove(authToken);
    }
}
