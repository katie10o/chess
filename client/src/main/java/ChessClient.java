import facade.ServerFacade;
import model.UserData;
import server.ResponseException;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private boolean signIn = false;
    private String authToken = null;

    public ChessClient(String url){
        server = new ServerFacade(url);
        serverUrl = url;
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
                case "createGame" -> createGame(params);
                case "joinGame" -> joinGame(params);
                case "clearDB" -> clearDB();
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
        return "Welcome, " + usr.username() + "\nWhat do you want to do?\n" + help();
    }
    private String signOut() {
        server.signOut();
        return "null";
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
    private String createGame(String[] params) {
        server.createGame(params);
        return "null";
    }
    private String joinGame(String[] params) {
        server.joinGame(params);
        return "null";
    }
    private String clearDB() {
        server.clearDB();
        return "null";
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
