package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthTokenData;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDataAccess implements DataAccess{


    public MySqlDataAccess() throws DataAccessException {
        configureDatebase();
    }
    private final String[] createUserTable = {
        """
            CREATE TABLE IF NOT EXISTS  user (
              `id` int NOT NULL AUTO_INCREMENT,
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
              `id` int NOT NULL AUTO_INCREMENT,
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
            CREATE TABLE IF NOT EXISTS  auth (
              `id` int NOT NULL AUTO_INCREMENT,
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
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)){
            queryStatement.setString(1, usrData.username());
            queryStatement.setString(2, usrData.password());
            queryStatement.setString(3, usrData.email());
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }

    }

    @Override
    public boolean checkUserName(String username) throws DataAccessException {
        String sql = "SELECT username FROM user WHERE username=?";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
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
        String sql = "INSERT INTO auth (token, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
            queryStatement.setString(1, tokenData.authToken());
            queryStatement.setString(2, tokenData.username());
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clearDB() throws DataAccessException {
        List<String> clearTables = List.of("TRUNCATE user", "TRUNCATE game", "TRUNCATE auth");
        for (String sql : clearTables){
            try (var conn = DatabaseManager.getConnection();
                var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
                queryStatement.executeUpdate();

            } catch (SQLException ex){
                throw new DataAccessException(ex.getMessage());
            }
        }
    }

    @Override
    public String getUserPassword(String userName) throws DataAccessException {
        String sql = "SELECT password FROM user WHERE username=?";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)){
            queryStatement.setString(1, userName);
            try (var resultStatement = queryStatement.executeQuery()){
                if (resultStatement.next()) {
                    return resultStatement.getString("password");
                }
                else {
                    throw new ResponseException(500, "unable to retrieve password");
                }
            }

        } catch (SQLException | ResponseException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clearAuthToken(String authToken) throws DataAccessException {
        var sql = "DELETE FROM auth WHERE token=?";
        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {

            queryStatement.setString(1, authToken);
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT token FROM auth WHERE token=?";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
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
        String sql = "INSERT INTO game (whiteUser, blackUser, gameName, chessGame) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
            queryStatement.setString(1, gameData.whiteUsername());
            queryStatement.setString(2, gameData.blackUsername());
            queryStatement.setString(3, gameData.gameName());
            queryStatement.setString(4, new Gson().toJson(new ChessGame()));
            queryStatement.executeUpdate();
            var rs = queryStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }else{
                throw new ResponseException(500, "error adding game");
            }

        } catch (SQLException | ResponseException ex){
            throw new DataAccessException(ex.getMessage());
        }

    }

    @Override
    public void editGame(GameData gameData) throws DataAccessException {
        String sql = "UPDATE game SET whiteUser = ?, blackUser = ? WHERE id = ?";
        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
            queryStatement.setString(1, gameData.whiteUsername());
            queryStatement.setString(2, gameData.blackUsername());
            queryStatement.setInt(3, gameData.gameID());
            queryStatement.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public GameData getGameData(GameData gameData) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE id=?";
        try (var conn = DatabaseManager.getConnection()) {
            var queryStatement = conn.prepareStatement(sql);
            queryStatement.setInt(1, gameData.gameID());
            try (var resultStatement = queryStatement.executeQuery()){
                if (resultStatement.next()) {
                    String whiteUser = resultStatement.getString("whiteUser");
                    String blackUser = resultStatement.getString("blackUser");
                    String gameName = resultStatement.getString("gameName");
                    String chessGame = resultStatement.getString("chessGame");

                    return new GameData(gameData.gameID(), whiteUser, blackUser, gameName, new Gson().fromJson(chessGame, ChessGame.class), null, null);
                }else{
                    throw new ResponseException(500, "error adding game");
                }
            }

        } catch (SQLException | ResponseException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public boolean checkGameID(GameData gameData) throws DataAccessException {
        String sql = "SELECT id FROM game WHERE id=?";

        try ( var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
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
        String sql = "SELECT username FROM auth WHERE token=?";

        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
            queryStatement.setString(1, authToken);
            try (var resultStatement = queryStatement.executeQuery()){
                if(resultStatement.next()){
                    return resultStatement.getString("username");
                } else{
                    throw new ResponseException(500, "error retrieving username");
                }
            }

        } catch (SQLException | ResponseException ex){
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() throws DataAccessException {
        HashMap<String, Collection<GameData>> result = new HashMap<>();
        Collection<GameData> gameList = new ArrayList<>();
        String sql = "SELECT * FROM game";
        try (var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql)) {
            try (var resultStatement = queryStatement.executeQuery()){
                while (resultStatement.next()) {
                    int gameID = resultStatement.getInt("id");
                    String whiteUser = resultStatement.getString("whiteUser");
                    String blackUser = resultStatement.getString("blackUser");
                    String gameName = resultStatement.getString("gameName");
                    String chessGame = resultStatement.getString("chessGame");

                    gameList.add(new GameData(gameID, whiteUser, blackUser, gameName, new Gson().fromJson(chessGame, ChessGame.class), null, null));
                }
            }

        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }
        result.put("games", gameList);
        return result;
    }
}
