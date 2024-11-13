import chess.ChessGame;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.util.*;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private boolean signIn = false;
    private String authToken = null;
    private HashMap<Integer, Integer> gameIDs = new HashMap<>();
    private HashMap<Integer, ChessGame> gameObjects = new HashMap<>();

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
                case "listgames" -> listGame();
                case "signout" -> signOut();
                case "creategame" -> createGame(params);
                case "playgame" -> joinGame(params);
                case "observegame" -> observeGame(params);
                case "cleardb" -> clearDB();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    private String drawBoard(ChessGame game){
        String strgame = game.getBoard().toString();
        return strgame;
    }

    private String observeGame(String[] params) {
        try {
            int tempID = gameIDs.get(Integer.parseInt(params[0]));
            return "Observing game: \n" + drawBoard(gameObjects.get(tempID));
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }

    private String signIn(String[] params) throws ResponseException {
        try{
            UserData user = new UserData(params[0], params[1], null, null);
            user = server.signIn(user);
            this.authToken = user.authToken();
            signIn = true;
            visitorName = params[0];
            return "Welcome, " + user.username() + "\nWhat do you want to do?\n" + help();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }

    }
    private String signOut() throws ResponseException {
        try {
            if (authToken != null){
                server.signOut(authToken);
                this.authToken = null;
                signIn = false;
                return "Goodbye!";
            } else {
                return "No authorization";
            }
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String register(String[] params) throws ResponseException {
        try {
            UserData user = new UserData(params[0], params[1], params[2], null);
            UserData usr = server.register(user);
            this.authToken = usr.authToken();
            signIn = true;
            return "Welcome, " + usr.username() + "\nWhat do you want to do?\n" + help();
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String listGame() throws ResponseException {
        try {
            Collection<GameData> games = server.listGame(authToken);
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
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String createGame(String[] params) throws ResponseException {
        try{
            GameData game = new GameData(0, null, null, params[0], null, null, authToken);
            server.createGame(game, authToken);
            return "Game " + params[0] + " is successfully created";
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String joinGame(String[] params) throws ResponseException {
        try {
            int tempID = gameIDs.get(Integer.parseInt(params[1]));
            GameData game = new GameData(tempID, null, null, null, null, params[0].toUpperCase(), null);
            server.joinGame(game, authToken);
            drawBoard(gameObjects.get(tempID));
            return "Game successfully joined\n" + drawBoard(gameObjects.get(tempID));
        } catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        } catch (NullPointerException ex){
            return "Must listGames first in order to play a game";
        }
        catch (Exception ex){
            return "Error occurred";
        }
    }
    private String clearDB() throws ResponseException {
        try {
            server.clearDB();
            return "Database cleared";
        }  catch (ResponseException | ServerException ex){
            return ex.getMessage();
        } catch (ArrayIndexOutOfBoundsException ex){
            return "Not enough parameters were given";
        }
        catch (Exception ex){
            return "Error occurred";
        }

    }

    public String help() {
        if (!signIn) {
            return """
                    - help
                    - quit
                    - register <username> <password> <email>
                    - signIn <username> <password>
                    """;
        }
        return """
                - help
                - signOut
                - createGame <gameName>
                - listGames
                - playGame <playerColor> <gameID>
                - observeGame <gameID>
                """;
    }
}
