package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthTokenData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAcess){
        this.dataAccess = dataAcess;

    }

    public Object addUser(UserData usrData) throws ResponseException {
        try {
            if (!dataAccess.checkUserName(usrData.username())){
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
        catch (DataAccessException e){
            throw new ResponseException(401, "Error: unauthorized");
        }
        catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void logOutUser(String authToken) throws ResponseException{
        try{
            if(dataAccess.getAuthToken(authToken)){
                dataAccess.clearAuthToken(authToken);
            }
            else {
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
    public int createGame(GameData gameData, String authToken) throws ResponseException{
        try{
            if(!dataAccess.getAuthToken(authToken)){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                return dataAccess.addGame(gameData);
            }
        }
        catch (ResponseException e){
            throw e;
        }
        catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void joinGame(GameData gameData, String authToken) throws ResponseException{
        try{
            if(!dataAccess.getAuthToken(authToken)){
                throw new ResponseException(401, "Error: unauthorized");
            }
            if (!dataAccess.checkGameID(gameData)){
                throw new ResponseException(400, "Error: bad request");
            }
            else {
                GameData currentGameData = dataAccess.getGameData(gameData);
                String userName = dataAccess.getUserName(authToken);
                if (currentGameData.whiteUsername() == null && gameData.playerColor().equals("WHITE")){
                    gameData = new GameData(currentGameData.gameID(), userName, currentGameData.blackUsername(),
                            currentGameData.gameName(), currentGameData.gameObject(), currentGameData.playerColor() );
                    dataAccess.editGame(gameData);
                }
                else if (currentGameData.blackUsername() == null && gameData.playerColor().equals("BLACK")){
                    gameData = new GameData(currentGameData.gameID(), currentGameData.whiteUsername(), userName,
                            currentGameData.gameName(), currentGameData.gameObject(), currentGameData.playerColor() );
                    dataAccess.editGame(gameData);
                }
                else {
                    throw new ResponseException(403, "Error: game taken");

                }
            }
        }
        catch (ResponseException e){
            throw e;
        }
        catch (Exception e){
            throw new ResponseException(500, e.getMessage());
        }
    }
    public HashMap<String, Collection<GameData>> getGames(String authToken) throws ResponseException{
        try{
            if(!dataAccess.getAuthToken(authToken)){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                return dataAccess.listGames();
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
