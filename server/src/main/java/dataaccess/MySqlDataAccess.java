package dataaccess;

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
        String sql2 = "SELECT * FROM user";

        try{
            var conn = DatabaseManager.getConnection();
            var queryStatement = conn.prepareStatement(sql, RETURN_GENERATED_KEYS);
            queryStatement.setInt(1, userID);
            queryStatement.setString(2, usrData.username());
            queryStatement.setString(3, usrData.password());
            queryStatement.setString(4, usrData.email());
            queryStatement.executeUpdate();


            try (var queryStatement2 = conn.prepareStatement(sql2)) {
                var resultStatement2 = queryStatement2.executeQuery();

                while (resultStatement2.next()){
                    System.out.println(resultStatement2.getString("username"));
                }
            }
        } catch (SQLException ex){
            throw new DataAccessException(ex.getMessage());
        }

        userID ++;
    }

    @Override
    public boolean checkUserName(String username) throws DataAccessException {
        return false;
    }

    @Override
    public void addAuthToken(AuthTokenData tokenData) throws DataAccessException {

    }

    @Override
    public void clearDB() throws DataAccessException {

    }

    @Override
    public String getUserPassword(String userName) throws DataAccessException {
        return "";
    }

    @Override
    public void clearAuthToken(String authToken) throws DataAccessException {

    }

    @Override
    public boolean getAuthToken(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public int addGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    @Override
    public void editGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGameData(GameData gameData) throws DataAccessException {
        return null;
    }

    @Override
    public boolean checkGameID(GameData gameData) throws DataAccessException {
        return false;
    }

    @Override
    public String getUserName(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public HashMap<String, Collection<GameData>> listGames() throws DataAccessException {
        return null;
    }
}
