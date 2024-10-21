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

    public boolean getUserData(String username){
        return userInfo.containsKey(username);
    }
    public void addAuthToken(AuthTokenData tokenData){
        authTokenInfo.put(tokenData.username(), tokenData);
    }

    @Override
    public void clearDB() {
        userInfo.clear();
        gameInfo.clear();
        authTokenInfo.clear();
    }

    public AuthTokenData getUserAndToken(String username){
        return authTokenInfo.get(username);
    }
}
