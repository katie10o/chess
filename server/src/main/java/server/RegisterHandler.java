package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    RegisterHandler(){

//    private final String gson;

//    RegisterHandler(Request request, Response response, ChesService service){
//        try{
//            UserData usrData = new Gson().fromJson(request.body(), UserData.class);
//            Object tokenData = service.addUser(usrData);
//            if (tokenData instanceof ResponseException){
//                throw (ResponseException) tokenData;
//            }else{
//                gson = new Gson().toJson(tokenData);
//            }
//        }
//        catch (ResponseException e){
//            int statusCode = e.StatusCode();
//            var message = e.getErrorMessage();
//            response.status(statusCode);
//            gson = new Gson().toJson(message);
//        }
    }

}
