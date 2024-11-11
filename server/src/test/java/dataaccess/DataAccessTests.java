package dataaccess;

import model.AuthTokenData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;


import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private static MySqlDataAccess dataAccess;

    UserData user1 = new UserData("user1", "password1", "email1");
    UserData user2 = new UserData("user2", "password2", "email2");
    GameData game1 = new GameData(0, null, null, "game1",
            null, null);
    GameData game2 = new GameData(0, null, null, null,
            null, null);
    AuthTokenData authToken1 = new AuthTokenData("user1", "abcdef12345");
    AuthTokenData authToken2 = new AuthTokenData("user2", "12345abcdef");



    @BeforeAll
    public static void init() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
    }

    @BeforeEach
    public void begin() throws DataAccessException {
        dataAccess.clearDB();
    }

    @Test
    @Order(1)
    @DisplayName("positive cast for addUser")
    public void addUser() throws DataAccessException {
        dataAccess.addUser(user1);
        assertTrue(dataAccess.checkUserName(user1.username()));
    }

    @Test
    @Order(2)
    @DisplayName("negative cast for addUser")
    public void addBadUser() throws DataAccessException, ResponseException {
        dataAccess.addUser(user1);
        assertFalse(dataAccess.checkUserName("badUser"));
    }

    @Test
    @Order(3)
    @DisplayName("positive cast for checkUser")
    public void checkUser() throws ResponseException, DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.checkUserName(user1.username());
        assertDoesNotThrow(() -> {
            dataAccess.checkUserName(user1.username());
        });
    }

    @Test
    @Order(4)
    @DisplayName("negative cast for checkUser")
    public void checkBadUser() throws ResponseException, DataAccessException {
        dataAccess.addUser(user1);
        assertFalse(dataAccess.checkUserName(user2.username()));
    }
    @Test
    @Order(5)
    @DisplayName("positive cast for addAuthData")
    public void addAuth() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.addAuthToken(authToken1);
        assertTrue(dataAccess.getAuthToken(authToken1.authToken()));
    }
    @Test
    @Order(6)
    @DisplayName("negative cast for addAuthData")
    public void addBadAuth() throws DataAccessException {
        dataAccess.addUser(user1);
        DataAccessException thrownException = assertThrows(
                DataAccessException.class,
                () -> dataAccess.addAuthToken(new AuthTokenData(null, null)),
                "Expected DataAcessException when trying to add null information for authData"
        );
    }

    @Test
    @Order(7)
    @DisplayName("positive cast for clearDB")
    public void clearDB() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.clearDB();
        assertFalse(dataAccess.checkUserName(user1.username()));
    }

    @Test
    @Order(8)
    @DisplayName("positive cast for getUserPass")
    public void getPass() throws DataAccessException {
        dataAccess.addUser(user1);
        String pass = dataAccess.getUserPassword(user1.username());
        assertEquals(user1.password(), pass);
    }

    @Test
    @Order(9)
    @DisplayName("negative cast for getUserPass")
    public void getBadPass() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.addUser(user2);
        String badPass = dataAccess.getUserPassword(user2.username());
        assertNotEquals(user1.password(), badPass);
    }
    @Test
    @Order(10)
    @DisplayName("positive cast for clearAuthToken")
    public void clearAuth() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.addAuthToken(authToken1);
        dataAccess.clearAuthToken(authToken1.authToken());
        assertFalse(dataAccess.getAuthToken(authToken1.authToken()));

    }

    @Test
    @Order(11)
    @DisplayName("negative cast for clearAuthToken")
    public void clearBadAuth() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.addAuthToken(authToken1);
        dataAccess.clearAuthToken(authToken2.authToken());
        assertTrue(dataAccess.getAuthToken(authToken1.authToken()));

    }

    @Test
    @Order(12)
    @DisplayName("positive cast for getAuthToken")
    public void getAuth() throws DataAccessException {
        dataAccess.addUser(user1);
        dataAccess.addAuthToken(authToken1);
        assertTrue(dataAccess.getAuthToken(authToken1.authToken()));

    }
    @Test
    @Order(13)
    @DisplayName("negative cast for getAuthToken")
    public void getBadAuth() throws DataAccessException {
        assertFalse(dataAccess.getAuthToken(authToken1.authToken()));

    }

    @Test
    @Order(14)
    @DisplayName("positive cast for addGame")
    public void addGame() throws DataAccessException {
        dataAccess.addUser(user1);
        int gameID = dataAccess.addGame(game1);
        assertTrue(gameID > 0);
    }

    @Test
    @Order(15)
    @DisplayName("negative cast for addGame")
    public void addBadGame() throws DataAccessException {
        dataAccess.addUser(user1);
        DataAccessException thrownException = assertThrows(
                DataAccessException.class,
                () -> dataAccess.addGame(game2),
                "Expected DataAcessException when trying to add null for game name"
        );
    }

    @Test
    @Order(16)
    @DisplayName("positive cast for editGame")
    public void editGame() throws DataAccessException {
        dataAccess.addUser(user1);
        int gameID = dataAccess.addGame(game1);
        dataAccess.editGame(new GameData(gameID, user1.username(), null, null, null, null));
        GameData gameTest = dataAccess.getGameData(new GameData(gameID, null,null,null,null,null));
        assertEquals(user1.username(), gameTest.whiteUsername());

    }

    @Test
    @Order(17)
    @DisplayName("negative cast for editGame")
    public void editBadGame() throws DataAccessException {
        dataAccess.addUser(user1);
        int gameID = dataAccess.addGame(game1);
        dataAccess.editGame(new GameData(gameID, user2.username(), null, null, null, null));
        GameData gameTest = dataAccess.getGameData(new GameData(gameID, null,null,null,null,null));
        assertNotNull(user1.username(), gameTest.whiteUsername());
    }

    @Test
    @Order(18)
    @DisplayName("positive cast for getGame")
    public void getGame() throws DataAccessException {

    }
    @Test
    @Order(19)
    @DisplayName("negative cast for getGame")
    public void getBadGame() throws DataAccessException {

    }

    @Test
    @Order(20)
    @DisplayName("positive cast for checkGameID")
    public void checkGameId() throws DataAccessException {

    }

    @Test
    @Order(21)
    @DisplayName("negative cast for checkGameID")
    public void checkBadGameId() throws DataAccessException {

    }
    
    @Test
    @Order(22)
    @DisplayName("positive cast for getUserName")
    public void getUserName() throws DataAccessException {

    }

    @Test
    @Order(23)
    @DisplayName("negative cast for getUserName")
    public void getadUserName() throws DataAccessException {

    }

    @Test
    @Order(24)
    @DisplayName("positive cast for listGames")
    public void listGames() throws DataAccessException {

    }

    @Test
    @Order(25)
    @DisplayName("negative cast for listGames")
    public void listBadGames() throws DataAccessException {

    }



}
