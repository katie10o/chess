package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc implements PieceMoveCalc {
    private ChessBoard board;
    private ChessPosition position;
    private ArrayList<ChessMove> possibleMoves;


    @Override
    public void PieceMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        possibleMoves = new ArrayList<>();

    }


    public enum typeOfMoves {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        LEFTUP,
        LEFTDOWN,
        RIGHTUP,
        RIGHTDOWN;

        public ChessPosition moves(ChessPosition pos){
            return switch (this) {
                case UP -> new ChessPosition(pos.getRow() + 1, pos.getColumn());
                case DOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn());
                case LEFT -> new ChessPosition(pos.getRow(), pos.getColumn() - 1);
                case RIGHT -> new ChessPosition(pos.getRow(), pos.getColumn() + 1);
                case LEFTUP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
                case LEFTDOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
                case RIGHTUP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
                case RIGHTDOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
            };
        }

    }

    public void validateMoves(){
        for (typeOfMoves move: typeOfMoves.values()){
            ChessPosition new_position = move.moves(position);
            if (board.getPiece(new_position) != null && board.extendsBoard(new_position)) {
                this.possibleMoves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));
            }
        }
    }
    public Collection<ChessMove> getPossibleMoves() {
        return possibleMoves;
    }

}
