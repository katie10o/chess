package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;
import model.AuthTokenData;
import model.GameData;
import model.UserData;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAcess){
        this.dataAccess = dataAcess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.clearDB();
    }

    public Object addUser(UserData usrData) throws ResponseException, DataAccessException {
            if (usrData.password() == null || usrData.password().isEmpty() || usrData.username() == null ||
                    usrData.username().isEmpty() || usrData.email() == null || usrData.email().isEmpty()){
                throw new ResponseException(400, "Error: bad request");
            }

            if (dataAccess.checkUserName(usrData.username())){
                throw new ResponseException(403, "Error: username already taken");
            }
            String hashpass = hashPassword(usrData.password());
            UserData newUserData = new UserData(usrData.username(), hashpass, usrData.email());

            dataAccess.addUser(newUserData);
            return giveToken(usrData.username());
    }

    public Object logInUser(UserData usrData) throws ResponseException, DataAccessException {
        if (usrData.password() == null || usrData.password().isEmpty() ||
                usrData.username() == null || usrData.username().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        if (!dataAccess.checkUserName(usrData.username())){
            throw new ResponseException(401, "Error: username does not exist - unauthorized");
        }

        String dataBasePassword = dataAccess.getUserPassword(usrData.username());
        if (!verifyPassword(usrData.password(), dataBasePassword)){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return giveToken(usrData.username());
    }

    public void logOutUser(String authToken) throws ResponseException, DataAccessException {
        if (authToken == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }

        dataAccess.clearAuthToken(authToken);
    }

    public int createGame(GameData gameData, String authToken) throws ResponseException, DataAccessException {
        if (gameData.gameName() == null || gameData.gameName().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        if (authToken == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return dataAccess.addGame(gameData);
    }

    public void joinGame(GameData gameData, String authToken) throws ResponseException, DataAccessException {
        if (gameData.playerColor() == null || gameData.playerColor().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }
        if (authToken == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (!dataAccess.checkGameID(gameData)){
            throw new ResponseException(400, "Error: bad request");
        }

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

    public HashMap<String, Collection<GameData>> getGames(String authToken) throws ResponseException, DataAccessException {
        if (authToken == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: unauthorized");
        }

        return dataAccess.listGames();
    }

    private static String generateToken(){
        return UUID.randomUUID().toString();
    }

    private Object giveToken(String username) throws DataAccessException {
        String newToken = generateToken();
        AuthTokenData tokenRecord = new AuthTokenData(username, newToken);
        dataAccess.addAuthToken(tokenRecord);
        return tokenRecord;
    }
    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    boolean verifyPassword(String clearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(clearTextPassword, hashedPassword);
    }
}
