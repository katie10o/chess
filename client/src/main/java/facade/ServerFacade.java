package facade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.UserData;
import server.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerFacade {
    private String url;

    public ServerFacade(String url){
        this.url = url;
    }

    public UserData signIn(UserData user) throws ResponseException {
        try {
            return makeRequest("POST", "/session", user, null, UserData.class);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

   public void signOut(String authToken) throws ResponseException {
       try {
           makeRequest("DELETE", "/session", null, authToken, null);
       } catch (Exception ex){
           throw new ResponseException(500, ex.getMessage());
       }
    }

    public UserData register(UserData user) throws ResponseException {
        try {
            return makeRequest("POST", "/user", user, null, UserData.class);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public Collection<GameData> listGame(String authToken) throws ResponseException {
        try{
            return makeRequest("GET", "/game", null, authToken, null);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void createGame(GameData game, String authToken) throws ResponseException {
        try{
            makeRequest("POST", "/game", game, authToken, GameData.class);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinGame(GameData game, String authToken) throws ResponseException {
        try{
            makeRequest("PUT", "/game", game, authToken, GameData.class);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void clearDB() throws ResponseException {
        try {
            makeRequest("DELETE", "/db", null, null, null);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
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
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
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
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
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
