package service;

import dataaccess.DataAccess;
import model.UserData;

import java.util.EmptyStackException;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAcess){
        this.dataAccess = dataAcess;
    }
    public String addUser(UserData usr){
//        wrap this in a try catch block
        if (!dataAccess.getUserData(usr.username())){
            dataAccess.addUser(usr.username(), usr.password(), usr.email());
            String token = generateToken();
            dataAccess.addAuthToken(usr.username(), token);
            return token;
        }
        else{
            return null;
        }
    }
    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
