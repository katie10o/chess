package dataaccess;

import model.AuthTokenData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import service.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private static Service service;
    UserData user1 = new UserData("user1", "password1", "email1");
    UserData user2 = new UserData("user2", "password2", "email2");
    AuthTokenData authTokenData;
    GameData createGame = new GameData(0, null, null, "test", null, null);
    int gameID;


    @BeforeAll
    public static void init() throws DataAccessException {
        service = new Service(new MySqlDataAccess());
    }

    @BeforeEach
    public void begin()  {
        authTokenData = null;
        gameID = 0;
    }

    @Test
    @Order(1)
    @DisplayName("register successful")
    public void addUser() throws ResponseException, DataAccessException {
        service.clearDB();
        Object authToken = service.addUser(new UserData("username", "password", "email"));
        assertNotNull(authToken);
    }

    @Test
    @Order(2)
    @DisplayName("register unsuccessful")
    public void addBadUser() throws DataAccessException, ResponseException {
        service.clearDB();
        service.addUser(new UserData("username", "password", "email"));
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.addUser(new UserData("username", "password", "email")),
                "Expected ResponseException when adding user that already exists"
        );
        assertEquals(403, thrownException.statusCode());
        assertEquals("Error: username already taken", thrownException.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("clear db successfully")
    public void clearAddedUser() throws ResponseException, DataAccessException {
        service.clearDB();
        service.addUser(user1);
        service.addUser(user2);
        service.clearDB();
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logInUser(user1),
                "Expected ResponseException when trying to log in a user on a cleared db"
        );
        assertEquals(401, thrownException.statusCode());
        assertEquals("Error: username does not exist - unauthorized", thrownException.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("clear db successfully")
    public void clearAddedGame() throws ResponseException, DataAccessException {
        service.clearDB();
        authTokenData = (AuthTokenData) service.addUser(user1);
        gameID = service.createGame(createGame, authTokenData.authToken());
        service.clearDB();
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.getGames(authTokenData.authToken()),
                "Expected ResponseException when trying to log in a user on a cleared db"
        );
        assertEquals(401, thrownException.statusCode());
        assertEquals("Error: unauthorized", thrownException.getMessage());
    }
    @Test
    @Order(5)
    @DisplayName("log in successfully")
    public void logInUser() throws ResponseException, DataAccessException {
        service.clearDB();
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
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);
        service.logOutUser(authTokenData.authToken());

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logInUser(new UserData("user1", "password2", "email1")),
                "Expected ResponseException when trying to log in a user with bad password"
        );
        assertEquals(401, thrownException.statusCode());
        assertEquals("Error: unauthorized", thrownException.getMessage());
    }
    @Test
    @Order(7)
    @DisplayName("log out successfully")
    public void logOutUser() throws ResponseException, DataAccessException {
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);

        assertDoesNotThrow(() -> {
            service.logOutUser(authTokenData.authToken());
        });
    }

    @Test
    @Order(8)
    @DisplayName("log out unsuccessfully with bad authToken")
    public void badAuthToken() throws ResponseException, DataAccessException {
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.logOutUser(authTokenData.authToken() + "11111"),
                "Expected ResponseException when trying to log out a user with bad auth token"
        );
        assertEquals(401, thrownException.statusCode());
        assertEquals("Error: unauthorized", thrownException.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("list games successfully")
    public void listGames() throws ResponseException, DataAccessException {
        service.clearDB();

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
        service.clearDB();

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
        assertEquals("Error: unauthorized", thrownException.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("create game successfully")
    public void createGame() throws ResponseException, DataAccessException {
        service.clearDB();

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
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.createGame(new GameData(0, null, null, null,
                        null, null), authTokenData.authToken()),
                "Expected ResponseException when trying to create game without game name"
        );
        assertEquals(400, thrownException.statusCode());
        assertEquals("Error: bad request", thrownException.getMessage());
    }

    @Test
    @Order(13)
    @DisplayName("join game successfully")
    public void joinGame() throws ResponseException, DataAccessException {
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);
        gameID = service.createGame(createGame, authTokenData.authToken());

        assertDoesNotThrow(() -> {
            service.joinGame(new GameData(gameID, null, null, null,
                    null, "WHITE"), authTokenData.authToken());
        });
        HashMap<String, Collection<GameData>> games = service.getGames(authTokenData.authToken());
        ArrayList<GameData> game = new ArrayList<>(games.get("games"));
        assertEquals(game.getFirst().whiteUsername(), "user1");

    }

    @Test
    @Order(14)
    @DisplayName("join game unsuccessfully, white user already taken")
    public void whiteUserTaken() throws ResponseException, DataAccessException {
        service.clearDB();

        authTokenData = (AuthTokenData) service.addUser(user1);
        AuthTokenData authTokenData2 = (AuthTokenData) service.addUser(new UserData("user2", "password2", "email2"));

        gameID = service.createGame(createGame, authTokenData.authToken());
        service.joinGame(new GameData(gameID, null, null, null,
                null, "WHITE"), authTokenData.authToken());

        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> service.joinGame(new GameData(gameID, null, null, null,
                        null, "WHITE"), authTokenData.authToken()),
                "Expected ResponseException when trying to join a game while a user is already white player"
        );
        assertEquals(403, thrownException.statusCode());
        assertEquals("Error: game taken", thrownException.getMessage());

        HashMap<String, Collection<GameData>> games = service.getGames(authTokenData.authToken());
        ArrayList<GameData> game = new ArrayList<>(games.get("games"));
        assertNotEquals(game.getFirst().whiteUsername(), "user2");

    }

}
