package gamui;

import chess.*;
import draw.DrawBoard;
import facade.ServerException;
import model.GameData;
import responseex.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserCommandMove;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;


public class GameClient {
    private final NotificationHandler notificationHandler;
    private final String url;

    private final ChessGame.TeamColor teamColor;
    private final String authToken;
    private final boolean player;
    private final GameData currentGame;

    private String outcome;

    public GameClient(boolean player, ChessGame.TeamColor teamColor, String cmd, String[] params, NotificationHandler notificationHandler,
                      String url, String authToken, GameData currentGame) throws ResponseException, ServerException {

        this.player = player;
        this.teamColor = teamColor;
        this.notificationHandler = notificationHandler;
        this.url = url;
        this.authToken = authToken;
        this.currentGame = currentGame;

        if (player){
            switch (cmd) {
                case "redraw" -> drawBoard();
                case "leave" -> leave();
                case "move" -> move(params);
                case "turn" -> turn();
                case "resign" -> resign();
                case "highlight" -> possibleMoves(params);
                default -> help();
        }
        } else {
            switch (cmd) {
                case "redraw" -> drawBoard();
                case "leave" -> leaveObserver();
                default -> help();
            }
        }
    }
    private UserCommandMove makeMoveMap(ChessMove movePiece){
        HashMap<String, HashMap<String, Integer>> move = new HashMap<>();
        move.put("startPosition", new HashMap<>());
        move.put("endPosition", new HashMap<>());
        move.get("startPosition").put("column", movePiece.getStartPosition().getColumn());
        move.get("startPosition").put("row", movePiece.getStartPosition().getRow());
        move.get("endPosition").put("column", movePiece.getEndPosition().getColumn());
        move.get("endPosition").put("row", movePiece.getEndPosition().getRow());
        move.put("promotion", new HashMap<>());
        ChessPiece.PieceType type = movePiece.getPromotionPiece();
        int promo;
        switch (type){
            case QUEEN -> promo = 0;
            case BISHOP -> promo = 1;
            case KNIGHT -> promo = 2;
            case ROOK -> promo = 3;
            case null -> promo = 4;
            case KING -> promo = 5;
            case PAWN -> promo = 6;
        }
        move.get("promotion").put("type", promo);
        return new UserCommandMove(move);
    }

    private void move(String[] params) {
        try {
            if (params.length < 1 || params.length > 3) {
                outcome = params.length < 1 ? "too few parameters given\n<column,row> to <column,row>"
                        : "too many parameters given\n<column,row> to <column,row>";
                return;
            }
            String[] oldLocation = params[0].contains(",") ? params[0].split(",") : params[0].split("");
            String[] newLocation = params[params.length - 1].contains(",") ? params[params.length - 1].split(",") : params[params.length - 1].split("");

            if (oldLocation.length != 2 || newLocation.length != 2) {
                outcome = "Incorrect column or row given";
                return;
            }
            if ((checkColumnValue(oldLocation[0].toLowerCase()) && checkColumnValue(newLocation[0])) &&
                (checkRowValue(Integer.valueOf(oldLocation[1])) && checkRowValue(Integer.valueOf(newLocation[1])))) {
                ChessPosition oldPosition = new ChessPosition(convertColumn(oldLocation[0]), Integer.parseInt(oldLocation[1]));
                ChessPosition newPosition = new ChessPosition(convertColumn(newLocation[0]), Integer.parseInt(newLocation[1]));

                var movePlay = new MovePlay(currentGame.gameObject(), oldPosition, newPosition, teamColor);
                ChessMove promoMove = movePlay.promoMoveChecker();
                UserCommandMove move = makeMoveMap(promoMove);
                outcome = "";

                WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
                ws.makeMove(authToken, currentGame.gameID(), move);
            }
        } catch (Exception ex){
            outcome = "Error: " + ex.getMessage();
        }
    }

    private void resign() throws ResponseException {
        WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
        ws.resign(authToken, currentGame.gameID());
        outcome =  "";
    }
    private void possibleMoves(String[] params) throws ResponseException {
        try {
            if (params.length != 1) {
                outcome = params.length < 1 ? "too few parameters given\n<column,row>" : "too many parameters given\n<column,row>";
                return;
            }
            String[] position = params[0].contains(",") ? params[0].split(",") : params[0].split("");

            if (position.length != 2) {
                outcome = "Incorrect column or row given";
                return;
            }
            if (checkColumnValue(position[0].toLowerCase())) {
                if (checkRowValue(Integer.valueOf(position[1].toLowerCase()))) {
                    ChessPosition startPosition = new ChessPosition(Integer.parseInt(position[1]), convertColumn(position[0].toLowerCase()));
                    DrawBoard draw = new DrawBoard(currentGame.gameObject().getBoard().getPiece(startPosition).getTeamColor(), currentGame.gameObject().getBoard().toString(), true, currentGame.gameObject().validMoves(startPosition));
                    outcome = draw.getDrawnBoard();
                }
            }
        }catch (ResponseException ex) {
            outcome = "Incorrect column or row";
        } catch (Exception ex){
            outcome = "Error highlighting";
        }
    }
    private void drawBoard(){
        DrawBoard draw = new DrawBoard(teamColor, currentGame.gameObject().getBoard().toString(), false, new ArrayList<>());
        outcome =  draw.getDrawnBoard();
    }
    private void turn(){
        ChessGame.TeamColor turn = currentGame.gameObject().getTeamTurn();
        outcome = "currently " + turn.toString() + "'s turn";
    }
    private boolean checkColumnValue(String c) throws ResponseException {
        try{
            return c.length() == 1 && c.charAt(0) >= 'a' && c.charAt(0) <= 'h';
        } catch (Exception ex){
            throw new ResponseException(400, "Incorrect column");
        }
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
    private boolean checkRowValue(Integer r) throws ResponseException {
        try{
            return r >= 1 && r <= 8;
        }catch (Exception ex){
            throw new ResponseException(400, "Incorrect row");
        }
    }
    private void leaveObserver() throws ResponseException {
        WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
        ws.leaveGame(authToken, currentGame.gameID());
        outcome =  "";
    }
    private void leave() {
        try {

            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
            ws.leaveGame(authToken, currentGame.gameID());
            outcome =  "";
        } catch (ResponseException ex){
            outcome =  "Error: " + ex.getMessage();
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
                    * highlight <column,row> - highlights the possible moves for a piece
                    * turn - returns what team color is in turn
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
