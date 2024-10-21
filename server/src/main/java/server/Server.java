package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthTokenData;
import model.GameData;
import model.UserData;
import service.ChessService;
import spark.*;

import java.util.HashMap;

public class Server {
    ChessService service = new ChessService(new MemoryDataAccess());

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


        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

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
            var authToken = request.headers("Authorization");
            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                GameData gameData = new Gson().fromJson(request.body(), GameData.class);
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
            var authToken = request.headers("Authorization");
            if (authToken == null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            else {
                GameData gameData = new Gson().fromJson(request.body(), GameData.class);
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
