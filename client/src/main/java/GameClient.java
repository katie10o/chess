import chess.*;
import facade.ServerException;
import facade.ServerFacade;
import model.GameData;
import responseex.ResponseException;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;


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

    public GameClient(boolean player, ChessGame.TeamColor teamColor, String cmd, String[] params,
                      String visitorName, ServerFacade facade, NotificationHandler notificationHandler,
                      String url, String authToken, GameData currentGame) throws ResponseException, ServerException {

        this.player = player;
        this.teamColor = teamColor;
        this.visitorName = visitorName;
        this.facade = facade;
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
    private void resign() throws ResponseException, ServerException {
        currentGame.gameObject().resignGame();
        facade.updateGame(currentGame, authToken);
        WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
        ws.resign(visitorName, authToken, currentGame.gameID());
        outcome =  "Game successfully resigned\n";
    }
    private void possibleMoves(String[] params) throws ResponseException {
        try {
            if (params.length < 1) {
                outcome = "too few parameters given\n<column,row>";
                return;
            }
            if (params.length > 1) {
                outcome = "too many parameters given\n<column,row>";
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
    private void move(String[] params){
        try{
            if (!currentGame.gameObject().getTeamTurn().equals(teamColor)){
                outcome = "not your turn to go";
                return;
            }
            if (params.length > 3){
                outcome = "too many parameters given\n<column,row> to <column,row>";
                return;
            }
            if (params.length < 1){
                outcome = "too few parameters given\n<column,row> to <column,row>";
                return;
            }
            String[] oldLocation = params[0].contains(",") ? params[0].split(",") : params[0].split("");
            String[] newLocation = params[params.length -1].contains(",") ? params[params.length -1].split(",") : params[params.length -1].split("");

            if (oldLocation.length != 2 || newLocation.length != 2){
                outcome = "Incorrect column or row given";
                return;
            }

            if (checkColumnValue(oldLocation[0].toLowerCase()) && checkColumnValue(newLocation[0])){
                if (checkRowValue(Integer.valueOf(oldLocation[1].toLowerCase())) && checkRowValue(Integer.valueOf(newLocation[1]))){
                    ChessGame game = currentGame.gameObject();

                    ChessPosition oldPosition = new ChessPosition(Integer.parseInt(oldLocation[1]), convertColumn(oldLocation[0].toLowerCase()));
                    ChessPosition newPosition = new ChessPosition(Integer.parseInt(newLocation[1]), convertColumn(newLocation[0].toLowerCase()));
                    ChessPiece currPiece = game.getBoard().getPiece(oldPosition);
                    ChessMove newMove = null;

                    if (!currPiece.getTeamColor().equals(teamColor)){
                        outcome = "cannot move piece that is not yours";
                        return;
                    }

                        if ((newPosition.getRow() == 1 && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK &&
                                currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor)) ||
                                (newPosition.getRow() == 8 && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) &&
                                        currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor)){

                            Scanner scanner = new Scanner(System.in);
                            int choice = -1;
                            while (choice < 1 || choice > 4){
                                System.out.println(">>> You selected a pawn, chose an upgrade: ");
                                System.out.println("\t1. Queen\n\t2. Bishop\n\t3. Knight\n\t4. Rook");
                                System.out.print(RESET_TEXT_COLOR + "\n>>> ");

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
                    if (validMoveChecker(newMove)) {
                        try{
                            currentGame.gameObject().makeMove(newMove);
                            facade.updateGame(currentGame, authToken);
                            outcome = "Move made";
                            WebSocketFacade ws = new WebSocketFacade(url, notificationHandler);
                            ws.makeMove(visitorName, teamColor.toString(), authToken, currentGame.gameID());
                            checkKings();
                        } catch (InvalidMoveException ex){
                            outcome = ex.getMessage();
                        }
                    } else {
                        outcome = "Cannot make that move, try again";
                    }
                }
            }
        } catch (ResponseException ex) {
            outcome = "Incorrect column or row";
        } catch (Exception ex){
            outcome = "Error making move";
        }
    }
    private void checkKings(){
        ChessGame game = currentGame.gameObject();
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            outcome = "White king checkmate, game over";
        }
        else if (game.isInStalemate(ChessGame.TeamColor.WHITE)){
            outcome = "White king in stalemate, game draw";
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            outcome = "White king in check, move to safety";
        }
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            outcome = "Black king checkmate, game over";
        }
        else if (game.isInStalemate(ChessGame.TeamColor.BLACK)){
            outcome = "Black king in stalemate, game draw";
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            outcome = "Black king in check, move to safety";
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
