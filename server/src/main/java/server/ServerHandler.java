package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;
import service.Service;
import spark.Request;

import java.util.HashMap;

public class ServerHandler {
    private final Service service;

    public ServerHandler(Service service) {
        this.service = service;
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

    public Object getGames(Request request) throws ResponseException, DataAccessException {
        var authToken = request.headers("Authorization");
        return new Gson().toJson(service.getGames(authToken));
    }
}
