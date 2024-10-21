package dataaccess;

import model.AuthTokenData;
import model.UserData;


public interface DataAccess {
    void addUser(UserData usrData);
    boolean getUserName(String username);
    void addAuthToken(AuthTokenData tokenData);
    void clearDB();
    String getUserPassword(String userName);


}
