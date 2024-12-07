package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;

public class UserCommandMove {

    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType pieceType;
    private HashMap<String, HashMap<String, Integer>> move;

    public UserCommandMove(HashMap<String, HashMap<String, Integer>> move){
        this.move = move;
    }
    public void setPostions(){
        int startCol = this.move.get("startPosition").get("column");
        int startRow = this.move.get("startPosition").get("row");
        int endCol = this.move.get("endPosition").get("column");
        int endRow = this.move.get("endPosition").get("row");
        if (this.move.containsKey("promotion")){
            int type = this.move.get("promotion").get("type");
            switch (type){
                case 0 -> pieceType = ChessPiece.PieceType.QUEEN;
                case 1 -> pieceType = ChessPiece.PieceType.BISHOP;
                case 2 -> pieceType = ChessPiece.PieceType.KNIGHT;
                case 3 -> pieceType = ChessPiece.PieceType.ROOK;
                case 4 -> pieceType = null;
            }
        } else {
            pieceType = null;
        }

        startPosition = new ChessPosition(startRow, startCol);
        endPosition = new ChessPosition(endRow, endCol);

    }

    public ChessPosition getEndPosition() { return endPosition; }
    public ChessPosition getStartPosition() { return startPosition;}
    public ChessPiece.PieceType getPieceType(){return pieceType;}
    public String getMove(){return move.toString();}
}



