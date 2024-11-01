package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthTokenData;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDataAccess implements DataAccess{
    int userID = 1500;
    int gameID = 150;
    int authTokenID = 15;


    public MySqlDataAccess() throws DataAccessException {
        configureDatebase();
    }
    private final String[] createUserTable = {
        """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createGameTable = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `id` int NOT NULL,
              `whiteUser` varchar(256),
              `blackUser` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGame` TEXT NOT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    private final String[] createAuthTable = {
            """
            CREATE TABLE IF NOT EXISTS  authToken (
              `id` int NOT NULL,
              `token` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(token)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatebase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createUserTable) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (var statement : createGameTable) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (var statement : createAuthTable) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void addUser(UserData usrData) throws DataAccessException {
        String sql = "INSERT INTO user (id, username, password, email) VALUES (?, ?, ?, ?)";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.setInt(1, userID);
            queryStatement.setString(2, usrData.username());
            queryStatement.setString(3, usrData.password());
            queryStatement.setString(4, usrData.email());
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }

        userID ++;
    }

    @Override
    public boolean checkUserName(String username) throws DataAccessException {
        String sql = "SELECT username FROM user WHERE username=?";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setString(1, username);
            try (var resultStatement = queryStatement.executeQuery()){
                return resultStatement.next();
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void addAuthToken(AuthTokenData tokenData) throws DataAccessException {
        String sql = "INSERT INTO authToken (id, token, username) VALUES (?, ?, ?)";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.setInt(1, authTokenID);
            queryStatement.setString(2, tokenData.authToken());
            queryStatement.setString(3, tokenData.username());
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
        authTokenID ++;
    }

    @Override
    public void clearDB() throws DataAccessException {
        var sql = "TRUNCATE chess";
        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }

    }

    @Override
    public String getUserPassword(String userName) throws DataAccessException {
        String sql = "SELECT password FROM user WHERE username=?";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setString(1, userName);
            try (var resultStatement = queryStatement.executeQuery()){
                return resultStatement.getString("password");
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clearAuthToken(String authToken) throws DataAccessException {
        var sql = "DELETE FROM authtoken WHERE token=?";
        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.setString(1, authToken);
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT token FROM authToken WHERE token=?";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setString(1, authToken);
            try (var resultStatement = queryStatement.executeQuery()){
                return resultStatement.next();
            }
        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int addGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO game (id, whiteUser, blackUser, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.setInt(1, gameID);
            queryStatement.setString(2, gameData.whiteUsername());
            queryStatement.setString(3, gameData.blackUsername());
            queryStatement.setString(4, gameData.gameName());
            queryStatement.setString(5, new Gson().toJson(new ChessGame()));
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
        int currGameId = gameID;
        gameID ++;
        return currGameId;
    }

    @Override
    public void editGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGameData(GameData gameData) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE id=?";
        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setInt(1, gameData.gameID());
            try (var resultStatement = queryStatement.executeQuery()){
                String whiteUser = resultStatement.getString("whiteUser");
                String blackUser = resultStatement.getString("blackUser");
                String gameName = resultStatement.getString("gameName");
                String chessGame = resultStatement.getString("chessGame");

                return new GameData(gameData.gameID(), whiteUser, blackUser, gameName, new Gson().fromJson(chessGame, ChessGame.class), null);
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean checkGameID(GameData gameData) throws DataAccessException {
        String sql = "SELECT id FROM game WHERE id=?";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setInt(1, gameData.gameID());
            try (var resultStatement = queryStatement.executeQuery()){
                return resultStatement.next();
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public String getUserName(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM authToken WHERE token=?";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setString(1, authToken);
            try (var resultStatement = queryStatement.executeQuery()){
                return resultStatement.getString("username");
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() throws DataAccessException {
        return null;
    }
}
