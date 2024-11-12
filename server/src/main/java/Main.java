import server.Server;

public class Main {
    public static void main(String[] args) {
        try{
            Server server = new Server();
            server.run(8080);
        }
        catch (Exception ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}