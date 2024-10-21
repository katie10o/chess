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
            if (!dataAccess.getUserName(usrData.username())){
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
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
    public void clearDB() throws ResponseException {
        try{
            dataAccess.clearDB();
        }
        catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Object logInUser(UserData usrData) throws ResponseException{
        try{
            String dataBasePassword = dataAccess.getUserPassword(usrData.username());
            if (usrData.password().equals(dataBasePassword)){
                String token = generateToken();
                AuthTokenData tokenData = new AuthTokenData(usrData.username(), token);
                dataAccess.addAuthToken(tokenData);
                return tokenData;
            }
            else{
                throw new ResponseException(401, "Error: unauthorized");
            }
        }
        catch (ResponseException e){
            throw e;
        }
        catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }
}
