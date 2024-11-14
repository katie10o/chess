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
                throw new ResponseException(400, "Error: Not enough parameters");
            }

            if (dataAccess.checkUserName(usrData.username())){
                throw new ResponseException(403, "Error: Username already taken");
            }
            String hashpass = hashPassword(usrData.password());
            UserData newUserData = new UserData(usrData.username(), hashpass, usrData.email(), null);

            dataAccess.addUser(newUserData);
            return giveToken(usrData.username());
    }

    public Object logInUser(UserData usrData) throws ResponseException, DataAccessException {
        if (usrData.password() == null || usrData.password().isEmpty() ||
                usrData.username() == null || usrData.username().isEmpty()){
            throw new ResponseException(400, "Error: Not enough parameters");
        }
        if (!dataAccess.checkUserName(usrData.username())){
            throw new ResponseException(401, "Error: Username does not exist");
        }

        String dataBasePassword = dataAccess.getUserPassword(usrData.username());
        if (!verifyPassword(usrData.password(), dataBasePassword)){
            throw new ResponseException(401, "Error: Wrong password");
        }

        return giveToken(usrData.username());
    }

    public void logOutUser(String authToken) throws ResponseException, DataAccessException {
        if (authToken == null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: Unauthorized");
        }

        dataAccess.clearAuthToken(authToken);
    }

    public int createGame(GameData gameData, String authToken) throws ResponseException, DataAccessException {
        if (gameData.gameName() == null || gameData.gameName().isEmpty()){
            throw new ResponseException(400, "Error: Not enough parameters");
        }
        if (authToken == null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: Unauthorized");
        }

        return dataAccess.addGame(gameData);
    }

    public void joinGame(GameData gameData, String authToken) throws ResponseException, DataAccessException {
        if (gameData.playerColor() == null || gameData.playerColor().isEmpty()){
            throw new ResponseException(400, "Error: Not enough parameters");
        }
        if (authToken == null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        if (!dataAccess.checkGameID(gameData)){
            throw new ResponseException(400, "Error: Bad gameID");
        }

        GameData currentGameData = dataAccess.getGameData(gameData);
        String userName = dataAccess.getUserName(authToken);
        if (!gameData.playerColor().equals("WHITE") && !gameData.playerColor().equals("BLACK")){
            throw new ResponseException(400, "Error: incorrect team color");
        }

        if (currentGameData.whiteUsername() == null && gameData.playerColor().equals("WHITE")){
            gameData = new GameData(currentGameData.gameID(), userName, currentGameData.blackUsername(),
                    currentGameData.gameName(), currentGameData.gameObject(), currentGameData.playerColor(), null );
            dataAccess.editGame(gameData);
        }
        else if (currentGameData.blackUsername() == null && gameData.playerColor().equals("BLACK")){
            gameData = new GameData(currentGameData.gameID(), currentGameData.whiteUsername(), userName,
                    currentGameData.gameName(), currentGameData.gameObject(), currentGameData.playerColor(), null );
            dataAccess.editGame(gameData);
        }
        else {
            throw new ResponseException(403, "Error: Position already taken");
        }
    }

    public HashMap<String, Collection<GameData>> getGames(String authToken) throws ResponseException, DataAccessException {
        if (authToken == null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        if(!dataAccess.getAuthToken(authToken)){
            throw new ResponseException(401, "Error: Unauthorized");
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
