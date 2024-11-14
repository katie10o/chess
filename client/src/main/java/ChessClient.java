import chess.ChessGame;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import model.UserData;
import responseex.ResponseException;

import java.util.*;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade facade;
    private boolean signIn = false;
    private String authToken = null;
    private HashMap<Integer, Integer> gameIDs = new HashMap<>();
    private HashMap<Integer, ChessGame> gameObjects = new HashMap<>();

    public ChessClient(String url){
        facade = new ServerFacade(url);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (signIn){
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
        }
    }
    private String drawBoard(ChessGame game){
        DrawBoard draw = new DrawBoard(game.getBoard().toString());
        return draw.getDrawnBoard();
    }

    private String observeGame(String[] params) {
        try {
            if (params.length != 1){
                if (params.length < 1){
                    return "Don't forget to add the game number!";
                } else{
                    return "Too many parameters given";
                }
            }
            try {
                int gameNumber = Integer.parseInt(params[0]);
                if (!gameIDs.containsKey(gameNumber)){
                    return "Game number does not exist";
                }
                int tempID = gameIDs.get(Integer.parseInt(params[0]));
                return "Observing game: \n" + drawBoard(gameObjects.get(tempID));
            } catch (NumberFormatException e) {
                return "Game number not a digit";
            }
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }

    private String signIn(String[] params) throws ResponseException {
        try{
            if (params.length != 2){
                if (params.length < 2){
                    return "missing parameters, enter username followed by password";
                } else{
                    return "Too many parameters given";
                }
            }
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
            if (params.length != 3){
                if (params.length < 3){
                    return "missing parameters, enter username, password, and email";
                } else{
                    return "Too many parameters given";
                }
            }
            UserData user = new UserData(params[0], params[1], params[2], null);
            UserData usr = facade.register(user);
            this.authToken = usr.authToken();
            signIn = true;
            return "Welcome, " + usr.username() + "\nWhat do you want to do?\n" + help();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String listGame() throws ResponseException {
        try {
            Collection<GameData> games = facade.listGame(authToken);
            StringBuilder gameInfo = new StringBuilder();
            gameInfo.append("Games: \n");
            int counter = 1;
            for (GameData game : games) {
                gameIDs.put(counter, game.gameID());
                gameObjects.put(game.gameID(), game.gameObject());
                gameInfo.append("\t").append(counter).append(". game name: ").append(game.gameName()).append("\n\t   white player: ")
                        .append(game.whiteUsername()).append("\n\t   black player: ").append(game.blackUsername()).append("\n");
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
            if (params.length != 1){
                if (params.length < 1){
                    return "missing parameters, enter game name";
                } else{
                    return "Too many parameters given";
                }
            }
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
            if (params.length != 2){
                if (params.length < 2){
                    return "missing parameters, enter player color followed by game number";
                } else{
                    return "Too many parameters given";
                }
            }
            try {
                int gameNumber = Integer.parseInt(params[1]);
                if (!gameIDs.containsKey(gameNumber)){
                    return "Game number does not exist";
                }
                int tempID = gameIDs.get(Integer.parseInt(params[1]));
                GameData game = new GameData(tempID, null, null, null, null, params[0].toUpperCase(), null);
                facade.joinGame(game, authToken);
                drawBoard(gameObjects.get(tempID));
                return "Game successfully joined\n" + drawBoard(gameObjects.get(tempID));
            } catch (NumberFormatException e) {
                return "Game number not a digit, make sure it comes after player color";
            }
        } catch (ResponseException | ServerException ex){
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
        if (!signIn) {
            return """
                    * help
                    * quit
                    * register <username> <password> <email>
                    * signIn <username> <password>
                    """;
        }
        return """
                * help
                * signOut
                * create <gameName> - creates a game
                * list - lists current games
                * play <playerColor> <gameID> - joins a game
                * observe <gameID> - observes a game
                """;
    }
}
