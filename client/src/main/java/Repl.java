import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String url){
        client = new ChessClient(url, this);
    }
    public void run() {
        System.out.println(WHITE_KING + "Welcome to 240 Chess Game. Sign in or register to start." + BLACK_KING);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            System.out.println(SET_TEXT_COLOR_RED + "Error: " + notification.getMessage());
        }
        else {
            System.out.println(SET_TEXT_COLOR_BLUE + notification.getMessage());
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }
}
