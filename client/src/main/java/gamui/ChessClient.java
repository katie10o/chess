package gamui;

import chess.ChessGame;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import model.UserData;
import responseex.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.*;

public class ChessClient {
    private final ServerFacade facade;
    private final NotificationHandler notificationHandler;
    private final String url;


    private String visitorName = null;
    private ChessGame.TeamColor teamColor;
    private boolean signIn = false;
    private boolean inGamePlay = false;
    private boolean inGameObserve = false;
    private GameData currentGame;
    private String authToken = null;

    private HashMap<Integer, Integer> gameIDs;
    private HashMap<Integer, GameData> gameObjects;

    public ChessClient(String url, NotificationHandler notificationHandler){
        facade = new ServerFacade(url);
        this.url = url;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (inGamePlay || inGameObserve){
                listGame();
                GameClient game = new GameClient(inGamePlay, teamColor, cmd, params,
                        notificationHandler, url, authToken, currentGame);
                String outcome = game.toString();
                if (Objects.equals(outcome, "Game successfully left\n")){
                    inGameObserve = false;
                    inGamePlay = false;
                }
                return outcome;
            }
            else if (signIn){
                return switch (cmd) {
                    case "list" -> listGame();
                    case "signout" -> signOut();
                    case "create" -> createGame(params);
                    case "play" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "cleardb" -> clearDB();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            else {
                    return switch (cmd) {
                        case "signin" -> signIn(params);
                        case "register" -> register(params);
                        case "cleardb" -> clearDB();
                        case "quit" -> "quit";
                        default -> help();
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (ServerException e) {
            throw new RuntimeException(e);
        }
    }

    private String observeGame(String[] params) {
        try {
            if (params.length != 1){ return params.length < 1 ? "Don't forget to add the game number!" : "Too many parameters given"; }

            int gameNumber = Integer.parseInt(params[0]);
            if (!gameIDs.containsKey(gameNumber)){ return "Game number does not exist";}
            int gameID = gameIDs.get(gameNumber);

            inGameObserve = true;
            teamColor = ChessGame.TeamColor.WHITE;
            currentGame = gameObjects.get(gameID);

            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
            ws.observeGame(authToken, gameID);

            return "";
        } catch (NumberFormatException e) {
            return "Game number not a digit";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }

    private String signIn(String[] params) throws ResponseException {
        try{
            if (params.length != 2){ return params.length < 2 ? "missing parameters, enter username followed by password" : "Too many parameters given";}

            UserData user = new UserData(params[0], params[1], null, null);
            user = facade.signIn(user);
            this.authToken = user.authToken();
            signIn = true;
            visitorName = params[0];

            return "Welcome, " + user.username() + "\nWhat do you want to do?\n" + help();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }

    }
    private String signOut() throws ResponseException {
        try {
            if (authToken != null){
                facade.signOut(authToken);
                this.authToken = null;
                signIn = false;
                return "Goodbye!\n";
            } else {
                return "No authorization";
            }
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String register(String[] params) throws ResponseException {
        try {
            if (params.length != 3){ return params.length < 3 ? "missing parameters, enter username, password, and email" :"Too many parameters given";}

            UserData user = new UserData(params[0], params[1], params[2], null);
            UserData usr = facade.register(user);
            this.authToken = usr.authToken();
            signIn = true;
            visitorName = usr.username();
            return "Welcome, " + visitorName + "\nWhat do you want to do?\n" + help();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String listGame() throws ResponseException {
        try {
            gameIDs = new HashMap<>();
            gameObjects = new HashMap<>();
            Collection<GameData> games = facade.listGame(authToken);
            StringBuilder gameInfo = new StringBuilder();
            gameInfo.append("Games: \n");
            int counter = 1;
            for (GameData game : games) {
                gameIDs.put(counter, game.gameID());
                gameObjects.put(game.gameID(), game);
                gameInfo.append("\t").append(counter).append(". game name: ").append(game.gameName()).append("\n\t   white player: ")
                        .append(game.whiteUsername() != null ? game.whiteUsername() : "None").append("\n\t   black player: ")
                        .append(game.blackUsername() != null ? game.blackUsername()  : "None").append("\n");
                counter++;
            }
            return gameInfo.toString();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String createGame(String[] params) throws ResponseException {
        try{
            if (params.length != 1){ return params.length < 1 ? "missing parameters, enter game name" : "Too many parameters given";}
            GameData game = new GameData(0, null, null, params[0], null, null, authToken);
            facade.createGame(game, authToken);
            return "Game " + params[0] + " is successfully created";
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String joinGame(String[] params) throws ResponseException {
        try {
            if (params.length != 2){ return params.length < 2 ? "missing parameters, enter player color followed by game number" : "Too many parameters given";}

            int gameNumber = Integer.parseInt(params[1]);
            if (!gameIDs.containsKey(gameNumber)){return "Game number does not exist";}
            int gameID = gameIDs.get(gameNumber);

            teamColor = params[0].equalsIgnoreCase("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            GameData game = new GameData(gameID, null, null, null, null, params[0].toUpperCase(), null);
            facade.joinGame(game, authToken);

            inGamePlay = true;
            currentGame = gameObjects.get(gameID);

            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
            ws.joinGame(authToken, gameID);
            return "";

        } catch (NumberFormatException e) {
            return "Game number not a digit, make sure it comes after player color";
        } catch (ResponseException ex){
            return ex.getMessage();
        } catch (NullPointerException ex){
            return "Must list games first in order to play a game";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String clearDB() throws ResponseException {
        try {
            facade.clearDB();
            return "Database cleared";
        }  catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }

    }

    public String help() {
        if (signIn) {
            return """
                    * help
                    * signOut
                    * create <gameName> - creates a game
                    * list - lists current games
                    * play <playerColor> <gameID> - joins a game
                    * observe <gameID> - observes a game
                    """;
        }
        else {
            return """
                    * help
                    * quit
                    * register <username> <password> <email>
                    * signIn <username> <password>
                    """;
        }
    }
}
