package client;

import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import responseex.ResponseException;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;
    private UserData user1 = new UserData("user1", "pass1", "email1", null);
    private UserData user2 = new UserData("user1", null, "email1", null);
    private GameData game1 = new GameData(0, null, null, "game1", null, null, null);


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }
    @AfterEach
    void clearDatabase() throws ServerException, ResponseException {
        facade.clearDB();
    }

    @AfterAll
    static void stopServer() throws ServerException, ResponseException {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("positive register")
    public void register() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        assertNotNull(response.authToken());
    }

    @Test
    @Order(2)
    @DisplayName("negative register")
    public void badRegister() throws ServerException, ResponseException {
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.register(user2),
                "Expected ResponseException when trying to register without password"
        );
        assertEquals("Error: Not enough parameters", thrownException.getMessage());

    }
    @Test
    @Order(3)
    @DisplayName("positive signin")
    public void signin() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.signOut(response.authToken());
        UserData response2 = facade.signIn(user1);
        assertNotNull(response2.authToken());
    }

    @Test
    @Order(4)
    @DisplayName("bad signin")
    public void badSignin() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.signOut(response.authToken());
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.signIn(new UserData("user1", "pass2", null, null)),
                "Expected ResponseException when trying to signin with bad password"
        );

        assertEquals("Error: Wrong password", thrownException.getMessage());
    }
    @Test
    @Order(5)
    @DisplayName("positive signout")
    public void signout() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        assertDoesNotThrow(() -> {
            facade.signOut(response.authToken());
        });
    }
    @Test
    @Order(6)
    @DisplayName("negative signout")
    public void badSignout() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.signOut(response.authToken());
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.signOut(response.authToken()),
        "Expected ResponseException when trying to signout without being signed in first"
        );
        assertEquals("Error: Unauthorized", thrownException.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("positive create game")
    public void createGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        assertDoesNotThrow(() -> {
            facade.createGame(game1, response.authToken());
        });

    }

    @Test
    @Order(8)
    @DisplayName("negative create game")
    public void badCreateGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.createGame(new GameData(0, null, null,
                        null, null, null, null), response.authToken()),
                "Expected ResponseException when trying to create game without game name"
        );
        assertEquals("Error: Not enough parameters", thrownException.getMessage());

    }

    @Test
    @Order(9)
    @DisplayName("positive list games")
    public void listGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.createGame(game1, response.authToken());
        facade.createGame(game1, response.authToken());
        Collection<GameData> games = facade.listGame(response.authToken());
        assertEquals(games.size(), 2);
    }

    @Test
    @Order(10)
    @DisplayName("negative list games")
    public void badListGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.createGame(game1, response.authToken());
        facade.createGame(game1, response.authToken());
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.listGame(response.authToken() + "44444"),
                "Expected ResponseException when trying to list games with bad auth token"
        );
        assertEquals("Error: Unauthorized", thrownException.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("positive join games")
    public void joinGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.createGame(game1, response.authToken());
        facade.createGame(game1, response.authToken());
        Collection<GameData> games = facade.listGame(response.authToken());
        GameData game = games.iterator().next();
        assertDoesNotThrow(() -> {
            facade.joinGame(new GameData(game.gameID(), null, null, null,
                    null, "WHITE", null), response.authToken());
        });

    }
    @Test
    @Order(12)
    @DisplayName("negative join games")
    public void badJoinGame() throws ServerException, ResponseException {
        UserData response = facade.register(user1);
        facade.createGame(game1, response.authToken());
        facade.createGame(game1, response.authToken());
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.joinGame(new GameData(-1, null, null, null,
                        null, "WHITE", null), response.authToken()),
                "Expected ResponseException when trying to join game with bad id"
        );
        assertEquals("Error: Bad gameID", thrownException.getMessage());

    }
    @Test
    @Order(13)
    @DisplayName("positive clear db")
    public void clearDB() throws ServerException, ResponseException {
        facade.register(user1);
        facade.clearDB();
        ResponseException thrownException = assertThrows(
                ResponseException.class,
                () -> facade.signIn(user1),
                "Expected ResponseException when trying to join game with bad id"
        );
        assertEquals("Error: Username does not exist", thrownException.getMessage());

    }

}
