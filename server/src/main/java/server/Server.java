package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.GameData;
import responseex.ResponseException;
import server.websocket.WebSocketHandler;
import service.Service;
import spark.*;

import java.io.IOException;

public class Server {
    private ServerHandler handler;
    private WebSocketHandler webSocketHandler;



    public int run(int desiredPort) {
        Spark.port(desiredPort);

        try{
            handler = new ServerHandler(new Service(new MySqlDataAccess()));
            webSocketHandler = new WebSocketHandler();
        } catch (DataAccessException e){
            System.err.println(e.getMessage());
            System.err.println("Error: Cannot connect to the database.");
        }

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);

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

    private Object exceptionHandler(ResponseException e, Request request, Response response) {
        int statusCode = e.statusCode();
        var message = e.getErrorMessage();
        response.status(statusCode);
        return new Gson().toJson(message);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //handlers below
    private Object clear(Request request, Response response) {
        try{
            return handler.clear();
        } catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object register(Request request, Response response) {
        try{
            return handler.register(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object logIn(Request request, Response response) {
        try{
            return handler.logIn(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object logOut(Request request, Response response) {
        try{
            return handler.logOut(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object createGame(Request request, Response response) {
        try{
            return handler.createGame(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object joinGame(Request request, Response response){
        try{
            return handler.joinGame(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

    private Object getGames(Request request, Response response) {
        try{
            return handler.getGames(request);
        }
        catch (ResponseException e) {
            return exceptionHandler(e, request, response);
        }
        catch (DataAccessException e){
            return exceptionHandler(new ResponseException(500, e.getMessage()), request, response);
        }
    }

}
