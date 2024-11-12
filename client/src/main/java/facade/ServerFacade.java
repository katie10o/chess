package facade;

import com.google.gson.Gson;
import model.UserData;
import server.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private String url;

    public ServerFacade(String url){
        this.url = url;
    }

    public void signIn(String[] params) {
        System.out.println("signed in called");
    }

   public void signOut() {
        System.out.println("signed out called");
    }
    public void register(String[] params) throws ResponseException {
        try {
            UserData user = new UserData(params[0], params[1], params[2]);
            var returnedStuff = makeRequest("POST", "/user", user, UserData.class);
            System.out.println(returnedStuff);
        } catch (Exception ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void listGame(String[] params) {
        System.out.println("list game called");
    }
    public void createGame(String[] params) {
        System.out.println("create game called");
    }
    public void joinGame(String[] params) {
        System.out.println("join game called");
    }
    public void clearDB() {
        System.out.println("clearDB called");
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
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
