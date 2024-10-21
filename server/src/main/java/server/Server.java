package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exception.ResponseException;
import model.AuthTokenData;
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
        System.out.println(request.headers());
        return null;
//        try{
//            request.headers();
////            UserData usrData = new Gson().fromJson(request.headers(), AuthTokenData.class);
////            Object tokenData = service.logInUser(usrData);
////            return new Gson().toJson(tokenData);
//        }
//        catch (ResponseException e){
//            int statusCode = e.StatusCode();
//            var message = e.getErrorMessage();
//            response.status(statusCode);
//            return new Gson().toJson(message);
//        }
    }
}
