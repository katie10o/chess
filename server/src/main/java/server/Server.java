package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import service.ChessService;
import spark.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Server {
    private final ChessService service = new ChessService(new MemoryDataAccess());
//    private static final Map<String, List<String>> methodRequestRequirements = Map.ofEntries(
//            entry("register", List.of("username", "password", "email")),
//            entry("logIn", List.of("username", "password")),
//            entry("createGame", List.of("gameName")),
//            entry("joinGame", List.of("playerColor", "gameID"))
//    );

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::logIn);
        Spark.delete("/session", this::logOut);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::getGames);

        Spark.exception(ResponseException.class, this::exceptionHandler);
        Spark.awaitInitialization();
        return Spark.port();
    }


    private void exceptionHandler(ResponseException e, Request request, Response response) {
        response.status(e.StatusCode());
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //handlers below
    private Object clear(Request request, Response response) {
        try{
            service.clearDB();
            return new Gson().toJson(new HashMap<>());
        } catch (ResponseException e) {
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }

    private Object register(Request request, Response response) {
        try{
            UserData usrData = new Gson().fromJson(request.body(), UserData.class);
            if (usrData.password() == null || usrData.password().isEmpty() || usrData.username() == null ||
                    usrData.username().isEmpty() || usrData.email() == null || usrData.email().isEmpty()){
                throw new ResponseException(400, "Error: bad request");
            }
            Object tokenData = service.addUser(usrData);
            return new Gson().toJson(tokenData);
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }
    private Object logIn(Request request, Response response) {
        try{
            UserData usrData = new Gson().fromJson(request.body(), UserData.class);

            if (usrData.password() == null || usrData.password().isEmpty() ||
                    usrData.username() == null || usrData.username().isEmpty()){
                throw new ResponseException(400, "Error: bad request");
            }
            Object tokenData = service.logInUser(usrData);
            return new Gson().toJson(tokenData);
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }
    private Object logOut(Request request, Response response) {
        try{
            var authToken = request.headers("Authorization");

            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                service.logOutUser(authToken);
            }
            return new Gson().toJson(new HashMap<>());
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }
    private Object createGame(Request request, Response response) {
        try{
            GameData gameData = new Gson().fromJson(request.body(), GameData.class);
            var authToken = request.headers("Authorization");

            if (gameData.gameName() == null || gameData.gameName().isEmpty()){
                throw new ResponseException(400, "Error: bad request");
            }
            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                int gameID = service.createGame(gameData, authToken);
                return String.format("{ \"gameID\": \"%s\" }", gameID);
            }
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }
    private Object joinGame(Request request, Response response){
        try{
            GameData gameData = new Gson().fromJson(request.body(), GameData.class);
            var authToken = request.headers("Authorization");

            if (gameData.playerColor() == null || gameData.playerColor().isEmpty()){
                throw new ResponseException(400, "Error: bad request");
            }
            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                service.joinGame(gameData, authToken);
            }
            return new Gson().toJson(new HashMap<>());
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }
    private Object getGames(Request request, Response response) {
        try{
            var authToken = request.headers("Authorization");
            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                return new Gson().toJson(service.getGames(authToken));
            }
        }
        catch (ResponseException e){
            int statusCode = e.StatusCode();
            var message = e.getErrorMessage();
            response.status(statusCode);
            return new Gson().toJson(message);
        }
    }

}
