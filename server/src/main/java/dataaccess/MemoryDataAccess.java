package dataaccess;

import chess.ChessGame;
import model.AuthTokenData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess{
    HashMap<String, UserData> userInfo = new HashMap<>();
    HashMap<Integer, GameData> gameInfo = new HashMap<>();
    HashMap<String, AuthTokenData> authTokenInfo = new HashMap<>();
    private int nextID = 1;

    @Override
    public void addUser(UserData usrData) {
        userInfo.put(usrData.username(), usrData);
    }

    public boolean checkUserName(String username){
        return userInfo.containsKey(username);
    }
    public void addAuthToken(AuthTokenData tokenData){
        authTokenInfo.put(tokenData.authToken(), tokenData);
    }

    public boolean getAuthToken(String authToken){
        return authTokenInfo.containsKey(authToken);
    }

    @Override
    public int addGame(GameData gameData) {
        gameData = new GameData(nextID, null, null, gameData.gameName(), new ChessGame(), null);
        gameInfo.put(nextID, gameData);
        nextID ++;
        return gameData.gameID();
    }

    @Override
    public void editGame(GameData gameData) {
        gameInfo.put(gameData.gameID(), gameData);

    }

    @Override
    public GameData getGameData(GameData gameData) {
        return gameInfo.get(gameData.gameID());
    }

    @Override
    public boolean checkGameID(GameData gameData) {
        return gameInfo.containsKey(gameData.gameID());
    }

    @Override
    public String getUserName(String authToken) {
        return authTokenInfo.get(authToken).username();
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() {
        HashMap<String, Collection<GameData>> games = new HashMap<>();
        Collection<GameData> gameList = new ArrayList<>();
        for (Map.Entry<Integer, GameData> entry : gameInfo.entrySet()){
            gameList.add(entry.getValue());
        }
        games.put("games", gameList);
        return games;
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
