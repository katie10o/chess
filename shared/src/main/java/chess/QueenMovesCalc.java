package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalc implements PieceMoveCalc{
    private ChessBoard board;
    private ChessPosition position;
    private ArrayList<ChessMove> possibleMoves;

    @Override
    public void PieceMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        possibleMoves = new ArrayList<>();

    }

    private enum typeOfMoves{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        DIAGNALLEFTUP ,
        DIAGNALLEFTDOWN,
        DIAGNALRIGHTUP,
        DIAGNALRIGHTDOWN;

        public ChessPosition moves(ChessPosition pos){
            return switch (this) {
                case UP -> new ChessPosition(pos.getRow() + 1, pos.getColumn());
                case DOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn());
                case LEFT -> new ChessPosition(pos.getRow(), pos.getColumn() - 1);
                case RIGHT -> new ChessPosition(pos.getRow(), pos.getColumn() + 1);
                case DIAGNALLEFTUP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
                case DIAGNALLEFTDOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
                case DIAGNALRIGHTUP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
                case DIAGNALRIGHTDOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
            };
        }

    }

    @Override
    public void validateMoves() {
        for (typeOfMoves move: typeOfMoves.values()){
            ChessPosition new_position = move.moves(position);
            while (board.extendsBoard(new_position) && board.getPiece(new_position) == null){
                this.possibleMoves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));
                new_position = move.moves(position);
            }
            if (board.getPiece(new_position) != null) {
                this.possibleMoves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));
            }
        }
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return possibleMoves;
    }
}



