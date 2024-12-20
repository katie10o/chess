package service;


import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.*;
import responseex.ResponseException;
import model.AuthTokenData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    private static Service service;
    UserData user1 = new UserData("user1", "password1", "email1", null);
    UserData user2 = new UserData("user2", "", "email2", null);
    AuthTokenData authTokenData;
    GameData createGame = new GameData(0, null, null, "test", null, null, null);
    int gameID;


    @BeforeAll
    public static void init(){
        service = new Service(new MemoryDataAccess());
    }
    @BeforeEach
    public void begin() throws ResponseException, DataAccessException {
        service.clearDB();
        authTokenData = null;
        gameID = 0;
    }

    @Test
    @Order(1)
    @DisplayName("register normally")
    public void addUser() throws ResponseException, DataAccessException {
        Object authToken = service.addUser(user1);
        assertNotNull(authToken);
    }

    @Test
    @Order(2)
    @DisplayName("register without a password")
    public void addBadUser() {
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.addUser(user2),
                "Expected ResponseException when adding user without password"
        );
        assertEquals(400, thrownException.statusCode());
    }

    @Test
    @Order(3)
    @DisplayName("clear db successfully")
    public void clearAddedUser() throws ResponseException, DataAccessException {
        service.addUser(user1);
        service.clearDB();
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logInUser(user1),
                "Expected ResponseException when trying to log in a user on a cleared db"
        );
        assertEquals(401, thrownException.statusCode());
    }

    @Test
    @Order(4)
    @DisplayName("clear db successfully")
    public void clearAddedGame() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        gameID = service.createGame(createGame, authTokenData.authToken());
        service.clearDB();
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.getGames(authTokenData.authToken()),
                "Expected ResponseException when trying to log in a user on a cleared db"
        );
        assertEquals(401, thrownException.statusCode());
    }
    @Test
    @Order(5)
    @DisplayName("log in successfully")
    public void logInUser() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        service.logOutUser(authTokenData.authToken());
        AuthTokenData authTokenData2 = (AuthTokenData) service.logInUser(user1);

        assertNotNull(authTokenData2);
        assertNotEquals(authTokenData, authTokenData2);
    }
    @Test
    @Order(6)
    @DisplayName("log in unsuccessfully with bad password")
    public void badPassword() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        service.logOutUser(authTokenData.authToken());

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logInUser(new UserData("user1", "password2", "email1", null)),
                "Expected ResponseException when trying to log in a user with bad password"
        );
        assertEquals(401, thrownException.statusCode());
    }
    @Test
    @Order(7)
    @DisplayName("log out successfully")
    public void logOutUser() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);

        assertDoesNotThrow(() -> {
            service.logOutUser(authTokenData.authToken());
        });
    }

    @Test
    @Order(8)
    @DisplayName("log out unsuccessfully with bad authToken")
    public void badAuthToken() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logOutUser(authTokenData.authToken() + "11111"),
                "Expected ResponseException when trying to log out a user with bad auth token"
        );
        assertEquals(401, thrownException.statusCode());
    }

    @Test
    @Order(9)
    @DisplayName("list games successfully")
    public void listGames() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        service.createGame(createGame, authTokenData.authToken());
        service.createGame(createGame, authTokenData.authToken());
        service.createGame(createGame, authTokenData.authToken());

        HashMap<String, Collection<GameData>> games = service.getGames(authTokenData.authToken());
        assertEquals(3, games.get("games").size());
    }

    @Test
    @Order(10)
    @DisplayName("list games unsuccessfully with no auth token")
    public void noAuthToken() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        service.createGame(createGame, authTokenData.authToken());
        service.createGame(createGame, authTokenData.authToken());
        service.createGame(createGame, authTokenData.authToken());

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logOutUser(null),
                "Expected ResponseException when trying to get game lists without auth token"
        );
        assertEquals(401, thrownException.statusCode());
    }

    @Test
    @Order(11)
    @DisplayName("create game successfully")
    public void createGame() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        assertDoesNotThrow(() -> {
            gameID = service.createGame(createGame, authTokenData.authToken());
        });

        assertNotEquals(gameID, 0);
    }

    @Test
    @Order(12)
    @DisplayName("cannot create game without game name")
    public void notGameName() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.createGame(new GameData(0, null, null, null,
                        null, null, null), authTokenData.authToken()),
        "Expected ResponseException when trying to create game without game name"
        );
        assertEquals(400, thrownException.statusCode());
    }

    @Test
    @Order(13)
    @DisplayName("join game successfully")
    public void joinGame() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        gameID = service.createGame(createGame, authTokenData.authToken());

        assertDoesNotThrow(() -> {
            service.joinGame(new GameData(gameID, null, null, null,
                    null, "WHITE", null), authTokenData.authToken());
        });
        HashMap<String, Collection<GameData>> games = service.getGames(authTokenData.authToken());
        ArrayList<GameData> game = new ArrayList<>(games.get("games"));
        assertEquals(game.getFirst().whiteUsername(), "user1");

    }

    @Test
    @Order(14)
    @DisplayName("join game unsuccessfully, white user already taken")
    public void whiteUserTaken() throws ResponseException, DataAccessException {
        authTokenData = (AuthTokenData) service.addUser(user1);
        AuthTokenData authTokenData2 = (AuthTokenData) service.addUser(new UserData("user2", "password2", "email2", null));

        gameID = service.createGame(createGame, authTokenData.authToken());
        service.joinGame(new GameData(gameID, null, null, null,
                null, "WHITE", null), authTokenData.authToken());

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.joinGame(new GameData(gameID, null, null, null,
                        null, "WHITE", null), authTokenData.authToken()),
                "Expected ResponseException when trying to join a game while a user is already white player"
        );
        assertEquals(403, thrownException.statusCode());

        HashMap<String, Collection<GameData>> games = service.getGames(authTokenData.authToken());
        ArrayList<GameData> game = new ArrayList<>(games.get("games"));
        assertNotEquals(game.getFirst().whiteUsername(), "user2");

    }



}