package service;

import dataaccess.DataAccess;
import exception.ResponseException;
import model.AuthTokenData;
import model.UserData;

import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAcess){
        this.dataAccess = dataAcess;
    }

    public Object addUser(UserData usrData) throws ResponseException {
        try {
            if (!dataAccess.getUserData(usrData.username())){
                dataAccess.addUser(usrData);
                String token = generateToken();
                AuthTokenData tokenData = new AuthTokenData(usrData.username(), token);
                dataAccess.addAuthToken(tokenData);
                return tokenData;
            }
            else{
                throw new ResponseException(403, "Error: username already taken");
            }
        }
        catch (ResponseException e){
            return e;
        } catch (Exception e) {
            System.out.println("New error, unsure");
            return new ResponseException(500, "Error: unknown");
        }
    }
    public void clearDB() throws ResponseException {
        try{
            dataAccess.clearDB();
        }
        catch (Exception e){
            throw new ResponseException(500, "Error: cannot clear db");
        }
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
