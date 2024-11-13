import facade.ServerFacade;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private boolean signIn = false;
    private String authToken = null;

    public ChessClient(String url){
        server = new ServerFacade(url);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "register" -> register(params);
                case "listGame" -> listGame(params);
                case "signout" -> signOut();
                case "creategame" -> createGame(params);
                case "joingame" -> joinGame(params);
                case "cleardb" -> clearDB();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String signIn(String[] params) throws ResponseException {
        UserData usr = server.signIn(params);
        this.authToken = usr.authToken();
        signIn = true;
        visitorName = params[0];
        return "Welcome, " + usr.username() + "\nWhat do you want to do?\n" + help();
    }
    private String signOut() throws ResponseException {
        if (authToken != null){
            server.signOut(authToken);
            this.authToken = null;
            signIn = false;
        }
        return "Goodbye!";
    }
    private String register(String[] params) throws ResponseException {
        UserData usr = server.register(params);
        this.authToken = usr.authToken();
        signIn = true;
        return "Welcome, " + usr.username() + "\nWhat do you want to do?\n" + help();
    }
    private String listGame(String[] params) {
        server.listGame(params);
        return "null";
    }
    private String createGame(String[] params) throws ResponseException {
        GameData game = server.createGame(params, authToken);
        return "Game " + params[0] + " is successfully created. GameID: " + game.gameID();
    }
    private String joinGame(String[] params) {
        server.joinGame(params);
        return "null";
    }
    private String clearDB() throws ResponseException {
        server.clearDB();
        return "Database cleared";
    }

    public String help() {
        if (!signIn) {
            return """
                    - register user <username> <password> <email>
                    - signIn <username> <password>
                    - clearDB
                    - quit
                    """;
        }
        return """
                - listAllGames
                - createGame <gameName>
                - joinGame <playerColor> <gameID>
                - signOut
                - quit
                """;
    }
}
