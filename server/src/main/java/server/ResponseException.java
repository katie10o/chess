package server;


import java.util.HashMap;

public class ResponseException extends Exception {
    final private int statusCode;
    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public int statusCode(){return statusCode;}

    public HashMap<String, Object> getErrorMessage(){
        HashMap<String, Object> jsonObj = new HashMap<>();
        jsonObj.put("message", getMessage());
        return jsonObj;
    }


}
