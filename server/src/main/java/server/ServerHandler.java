package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import responseex.ResponseException;
import service.Service;
import spark.Request;
import server.websocket.WebSocketHandler;


import java.util.HashMap;

public class ServerHandler {
    private final Service service;

    public ServerHandler(Service service) {
        this.service =  service;
    }
    public Service getService(){
        return service;
    }

    public Object clear() throws ResponseException, DataAccessException {
        service.clearDB();
        return new Gson().toJson(new HashMap<>());
    }

    public Object register(Request request) throws ResponseException, DataAccessException {
        UserData usrData = new Gson().fromJson(request.body(), UserData.class);
        Object tokenData = service.addUser(usrData);
        return new Gson().toJson(tokenData);
    }

    public Object logIn(Request request) throws ResponseException, DataAccessException {
        UserData usrData = new Gson().fromJson(request.body(), UserData.class);
        Object tokenData = service.logInUser(usrData);
        return new Gson().toJson(tokenData);
    }

    public Object logOut(Request request) throws ResponseException, DataAccessException {
        var authToken = request.headers("Authorization");
        service.logOutUser(authToken);
        return new Gson().toJson(new HashMap<>());
    }

    public Object createGame(Request request) throws ResponseException, DataAccessException {
        GameData gameData = new Gson().fromJson(request.body(), GameData.class);
        var authToken = request.headers("Authorization");
        int gameID = service.createGame(gameData, authToken);
        return String.format("{ \"gameID\": \"%s\" }", gameID);
    }

    public Object joinGame(Request request) throws ResponseException, DataAccessException {
        GameData gameData = new Gson().fromJson(request.body(), GameData.class);
        var authToken = request.headers("Authorization");
        service.joinGame(gameData, authToken);

        return new Gson().toJson(new HashMap<>());
    }
    public Object getGame(Request request) throws ResponseException, DataAccessException {
        GameData gameData = new Gson().fromJson(request.body(), GameData.class);
        var authToken = request.headers("Authorization");
        return new Gson().toJson(service.getGame(authToken, gameData.gameID()));
    }

    public Object getGames(Request request) throws ResponseException, DataAccessException {
        var authToken = request.headers("Authorization");
        return new Gson().toJson(service.getGames(authToken));
    }
}
