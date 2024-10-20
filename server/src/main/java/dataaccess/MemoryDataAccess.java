package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    HashMap<String, HashMap<String, String>> userInfo = new HashMap<>();
    @Override
    public void addUser(String username, String password, String email) {
        HashMap<String, String> tempUserDetail = new HashMap<>();
        tempUserDetail.put("password", password);
        tempUserDetail.put("email", email);
        tempUserDetail.put("AuthToken", null);

        userInfo.put(username, tempUserDetail);
    }

    public boolean getUserData(String username){
        return userInfo.containsKey(username);
    }
    public void addAuthToken(String username, String token){
        userInfo.get(username).put("AuthToken", token);
    }
}
