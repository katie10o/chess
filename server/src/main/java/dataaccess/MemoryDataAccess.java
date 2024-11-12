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
    public void addUser(UserData usrData) throws DataAccessException{
        try{
            userInfo.put(usrData.username(), usrData);
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public boolean checkUserName(String username) throws DataAccessException{
        try{
            return userInfo.containsKey(username);
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public void addAuthToken(AuthTokenData tokenData) throws DataAccessException{
        try{
            authTokenInfo.put(tokenData.authToken(), tokenData);
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public boolean getAuthToken(String authToken) throws DataAccessException{
        try{
            return authTokenInfo.containsKey(authToken);
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int addGame(GameData gameData) throws DataAccessException{
        try{
            gameData = new GameData(nextID, null, null, gameData.gameName(), new ChessGame(), null, null);
            gameInfo.put(nextID, gameData);
            nextID ++;
            return gameData.gameID();
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void editGame(GameData gameData) throws DataAccessException{
        try{
            gameInfo.put(gameData.gameID(), gameData);
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGameData(GameData gameData) throws DataAccessException{
        try{
            return gameInfo.get(gameData.gameID());
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean checkGameID(GameData gameData) throws DataAccessException{
        try{
            return gameInfo.containsKey(gameData.gameID());
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getUserName(String authToken) throws DataAccessException{
        try{
            return authTokenInfo.get(authToken).username();
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() throws DataAccessException{
        try{
            HashMap<String, Collection<GameData>> games = new HashMap<>();
            Collection<GameData> gameList = new ArrayList<>();
            for (Map.Entry<Integer, GameData> entry : gameInfo.entrySet()){
                gameList.add(entry.getValue());
            }
            games.put("games", gameList);
            return games;
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearDB() throws DataAccessException{
        try{
            userInfo.clear();
            gameInfo.clear();
            authTokenInfo.clear();
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getUserPassword(String username) throws DataAccessException{
        try{
            return userInfo.get(username).password();
        }catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuthToken(String authToken) throws DataAccessException{
           try{
               authTokenInfo.remove(authToken);
           }catch (Exception e){
               throw new DataAccessException(e.getMessage());
           }
    }
}
