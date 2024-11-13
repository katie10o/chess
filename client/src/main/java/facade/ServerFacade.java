package facade;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.xdevapi.JsonParser;
import model.ErrorMessage;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerFacade {
    private String url;

    public ServerFacade(String url){
        this.url = url;
    }

    public UserData signIn(UserData user) throws ResponseException, ServerException {
        try {
            return makeRequest("POST", "/session", user, null, UserData.class);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

   public void signOut(String authToken) throws ResponseException, ServerException {
       try {
           makeRequest("DELETE", "/session", null, authToken, null);
       } catch (ResponseException ex){
           throw new ResponseException(ex.statusCode(), ex.getMessage());
       } catch (Exception ex){
           throw new ServerException("Cannot connect to server");
       }
    }

    public UserData register(UserData user) throws ResponseException, ServerException {
        try {
            return makeRequest("POST", "/user", user, null, UserData.class);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    public Collection<GameData> listGame(String authToken) throws ResponseException, ServerException {
        try{
            return makeRequest("GET", "/game", null, authToken, null);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    public void createGame(GameData game, String authToken) throws ResponseException, ServerException {
        try{
            makeRequest("POST", "/game", game, authToken, GameData.class);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    public void joinGame(GameData game, String authToken) throws ResponseException, ServerException {
        try{
            makeRequest("PUT", "/game", game, authToken, GameData.class);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    public void clearDB() throws ResponseException, ServerException {
        try {
            makeRequest("DELETE", "/db", null, null, null);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException, ServerException {
        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (Objects.equals(path, "/game") || (Objects.equals(method, "DELETE") && Objects.equals(path, "/session"))){
                http.setRequestProperty("Authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);

            if (Objects.equals(path, "/game") && Objects.equals(method, "GET")){
                try (InputStreamReader reader = new InputStreamReader(http.getInputStream())) {
                    Map<String, List<GameData>> responseMap = new Gson().fromJson(reader, TypeToken.getParameterized(Map.class, String.class, TypeToken.getParameterized(List.class, GameData.class).getType()).getType());
                    return (T) responseMap.get("games");
                }
            }

            return readBody(http, responseClass);
        }
        catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(), ex.getMessage());
        } catch (Exception ex){
            throw new ServerException("Cannot connect to server");
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        String message = http.getResponseMessage();
        if (!isSuccessful(status)) {
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null){
                String json = new BufferedReader(new InputStreamReader(errorStream)).lines().collect(Collectors.joining("\n"));
                ErrorMessage errorMessage = new Gson().fromJson(json, ErrorMessage.class);
                message = errorMessage.message();
                }
            }
            throw new ResponseException(status, "Error: " + message);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }
}
