import chess.*;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) {
        try{
            Server server = new Server();
            server.run(8080);
        }
        catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}