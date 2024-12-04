import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import responseex.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;


public class GameClient {
    private final String visitorName;
    private final ChessGame.TeamColor teamColor;
    private final ServerFacade facade;
    private final NotificationHandler notificationHandler;
    private final String url;
    private final String authToken;
    private final boolean player;
    private String outcome;
    private final GameData currentGame;

    public GameClient(boolean player, String teamColor, String cmd, String[] params,
                      String visitorName, ServerFacade facade, NotificationHandler notificationHandler,
                      String url, String authToken, GameData currentGame) throws ResponseException {

        if (teamColor.equalsIgnoreCase("white")){ this.teamColor = ChessGame.TeamColor.WHITE; }
        else { this.teamColor = ChessGame.TeamColor.BLACK;}

        this.player = player;
        this.visitorName = visitorName;
        this.facade = facade;
        this.notificationHandler = notificationHandler;
        this.url = url;
        this.authToken = authToken;
        this.currentGame = currentGame;

        if (player){
            switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
//                case "resign" -> resign();
//                case "possibleMoves" -> possibleMoves();
                default -> help();
        };

        } else {
            switch (cmd) {
//                case "redraw" -> listGame();
                case "leave" -> leaveObserver();
                default -> help();
            };
        }

    }
    private void redraw(){

    }

    private void move(String[] params){
        try{
            String[] oldLocation = params[0].split(",");
            String[] newLocation = params[params.length -1].split(",");
            if (checkColumnValue(oldLocation[0].toLowerCase()) && checkColumnValue(newLocation[0])){
                if (checkRowValue(Integer.valueOf(oldLocation[1].toLowerCase())) && checkRowValue(Integer.valueOf(newLocation[1]))){
                    ChessGame game = currentGame.gameObject();

                    ChessPosition oldPosition = new ChessPosition(Integer.parseInt(oldLocation[1]), convertColumn(oldLocation[0].toLowerCase()));
                    ChessPosition newPosition = new ChessPosition(Integer.parseInt(newLocation[1]), convertColumn(newLocation[0].toLowerCase()));
                    ChessPiece currPiece = game.getBoard().getPiece(oldPosition);
                    ChessMove newMove = null;

                        if ((newPosition.getRow() == 1 && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK &&
                                currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor)) ||
                                (newPosition.getRow() == 8 && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) &&
                                        currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor)){

                            Scanner scanner = new Scanner(System.in);
                            int choice = -1;
                            while (choice < 1 || choice > 4){
                                System.out.println(">>> You selected a pawn, chose an upgrade: ");
                                System.out.println("\t1. Queen\n\t2. Bishop\n\t3. Knight\n\t4. Rook\n\t>>> ");

                                if (scanner.hasNext()){
                                    choice = scanner.nextInt();
                                    switch (choice){
                                        case 1 -> newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.QUEEN);
                                        case 2 -> newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.BISHOP);
                                        case 3 -> newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.KNIGHT);
                                        case 4 -> newMove = new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.ROOK);
                                        default -> System.out.println("Invalid choice, choose between 1 through 4");
                                    }
                                } else {
                                    System.out.println("Invalid input. Please enter a number.");
                                    scanner.next();
                                }
                        }
                    } else {
                        newMove = new ChessMove(oldPosition, newPosition, null);
                    }
                    assert newMove != null;
                    if (validMoveChecker(newMove)) {
                        currentGame.gameObject().makeMove(newMove);
                        facade.updateGame(currentGame, authToken);
                        outcome = "Move made";
                    } else {
                        outcome = "Cannot make that move, try again";
                    }
                }
            }
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
            outcome = "Error making move";
        }
    }
    private boolean validMoveChecker(ChessMove newMove){
        ChessGame game = currentGame.gameObject();
        Collection<ChessMove> moves = game.validMoves(newMove.getStartPosition());
        for (ChessMove move : moves){
            if (move.equals(newMove)){
                return true;
            }
        }
        return false;
    }
    private boolean checkColumnValue(String c){
        return c != null && c.length() == 1 && c.charAt(0) >= 'a' && c.charAt(0) <= 'h';
    }
    private Integer convertColumn(String c){
        return switch (c){
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }
    private boolean checkRowValue(Integer r){
        return r != null && r >= 1 && r <= 8;
    }

    private void leaveObserver() throws ResponseException {
        WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
        ws.leaveGame(visitorName, authToken, currentGame.gameID());

        outcome =  "Game successfully left\n";
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

            GameData game = new GameData(currentGame.gameID(), whiteUser, blackUser, currentGame.gameName(),
                    currentGame.gameObject(), currentGame.playerColor(), currentGame.authToken());

            facade.leaveGame(game, authToken);

            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
            ws.leaveGame(visitorName, authToken, game.gameID());

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
                    * move <column,row> to <column,row> - begins to move chess peice, will prompt the column and row
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
