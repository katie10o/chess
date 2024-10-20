package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.UserData;
import service.ChessService;
import spark.*;

import java.util.logging.Handler;

public class Server {
    ChessService service = new ChessService(new MemoryDataAccess());

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.post("/user", this::register);
        Spark.delete("/session", this::clear);

        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //handlers below
    private Object clear(Request request, Response response) throws Exception{
        System.out.println("clear was called");
        return null;
    }

    private Object register(Request request, Response response) throws Exception{
        UserData usrData = new Gson().fromJson(request.body(), UserData.class);
        String token = service.addUser(usrData);
        System.out.format("Token: %s\n", token);
        return new Gson().toJson(token);
    }
}
