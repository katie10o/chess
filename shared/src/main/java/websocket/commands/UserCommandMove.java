package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;

public class UserCommandMove {

    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private HashMap<String, HashMap<String, Integer>> move;

    public UserCommandMove(HashMap<String, HashMap<String, Integer>> move){
        this.move = move;
    }
    public void setPostions(){
        int startCol = this.move.get("startPosition").get("column");
        int startRow = this.move.get("startPosition").get("row");
        int endCol = this.move.get("endPosition").get("column");
        int endRow = this.move.get("endPosition").get("row");

        startPosition = new ChessPosition(startRow, startCol);
        endPosition = new ChessPosition(endRow, endCol);

    }

    public ChessPosition getEndPosition() { return endPosition; }
    public ChessPosition getStartPosition() { return startPosition;}
    public String getMove(){return move.toString();}
}



