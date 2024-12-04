import chess.ChessGame;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import responseex.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.HashMap;
import java.util.Objects;

public class GameClient {
    private String visitorName;
    private final ServerFacade facade;
    private final NotificationHandler notificationHandler;
    private final String url;
    private String authToken;
    private boolean player;
    private String outcome;
    private GameData currentGame;

    public GameClient(boolean player, String cmd, String[] params,
                      String visitorName, ServerFacade facade, NotificationHandler notificationHandler,
                      String url, String authToken, GameData currentGame){

        this.player = player;
        this.visitorName = visitorName;
        this.facade = facade;
        this.notificationHandler = notificationHandler;
        this.url = url;
        this.authToken = authToken;
        this.currentGame = currentGame;

        if (player){
            switch (cmd) {
//                case "redraw" -> redraw();
                case "leave" -> leave();
//                case "move" -> move();
//                case "resign" -> resign();
//                case "possibleMoves" -> possibleMoves();
                default -> help();
        };

        } else {
            switch (cmd) {
//                case "redraw" -> listGame();
//                case "leave" -> signOut();
                default -> help();
            };
        }

    }


    private void leave() {
        try {
            String whiteUser = currentGame.whiteUsername();
            String blackUser = currentGame.blackUsername();
            if (Objects.equals(visitorName, whiteUser)){
                whiteUser = null;
            } else {
                blackUser = null;
            }
            String teamColor = whiteUser == null ? "WHITE" : "BLACK";

            GameData game = new GameData(currentGame.gameID(), whiteUser, blackUser, currentGame.gameName(),
                    currentGame.gameObject(), currentGame.playerColor(), currentGame.authToken());

            facade.leaveGame(game, authToken);

            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
            ws.leaveGame(visitorName, teamColor, authToken, game.gameID());

            outcome =  "Game successfully left\n";

        } catch (ResponseException | ServerException ex){
            outcome =  ex.getMessage();
        } catch (NullPointerException ex){
            outcome =  "Must list games first in order to play a game";
        }
        catch (Exception ex){
            outcome =  "Error occurred";
        }


    }

    private void help() {
        if (player){
            outcome = """
                    * redraw - redraws chessboard
                    * leave - leaves the chess game
                    * move - begins to move chess peice, will prompt the column and row
                    * resign - forfeits game, automatic loss
                    * possibleMoves - highlights the possible moves for a piece
                    """;
        }
        else {
            outcome = """
                    * redraw - redraws chessboard
                    * leave - leaves the game
                    """;
        }
    }
    public String toString(){
        return outcome;
    }
}
