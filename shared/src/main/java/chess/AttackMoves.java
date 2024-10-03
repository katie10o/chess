package chess;

import java.util.ArrayList;
import java.util.Collection;

public class AttackMoves {
    private ArrayList<ChessPosition> dangerPositions = new ArrayList<>();

    public  AttackMoves() {

    }
    public void validAttackMoves(ChessPiece.PieceType type, ChessPosition king_position, ChessPosition new_position) {
        switch (type) {
            case BISHOP -> bishopAttack(new_position, king_position);
            case QUEEN -> queenAttack(new_position, king_position);
            case ROOK -> rookAttack(new_position, king_position);
        }
    }


    private void addMoves(TypeOfMoves.moves move, ChessPosition attack_position, ChessPosition king_position ){
        while (king_position != attack_position){
            ChessPosition new_pos = move.movePositions(attack_position);
            dangerPositions.add(new_pos);
            attack_position = new_pos;
        }
    }

    private void bishopAttack(ChessPosition bishop_position, ChessPosition king_position){
        if (king_position.getRow() > bishop_position.getRow()){
//            left side of king
            if (king_position.getColumn() > bishop_position.getColumn()){
//                bottom side of king
                TypeOfMoves.moves move = TypeOfMoves.moves.DIAGONAL_RIGHT_UP;
                addMoves(move, bishop_position, king_position);
            }
            else{
//                upper side of king
                TypeOfMoves.moves move = TypeOfMoves.moves.DIAGONAL_RIGHT_DOWN;
                addMoves(move, bishop_position, king_position);
            }
        }
        else {
//            right side of king
            if (king_position.getColumn() > bishop_position.getColumn()){
                TypeOfMoves.moves move = TypeOfMoves.moves.DIAGONAL_LEFT_UP;
                // bottom side of king
                addMoves(move, bishop_position, king_position);
            }
            else {
//                upper side of king
                TypeOfMoves.moves move = TypeOfMoves.moves.DIAGONAL_LEFT_DOWN;
                addMoves(move, bishop_position, king_position);
            }
        }
    }
    private void rookAttack(ChessPosition rook_position, ChessPosition king_position){

        if (king_position.getRow() == rook_position.getRow()){
//            same row
            if (king_position.getColumn() > rook_position.getColumn()){
//                left side
                TypeOfMoves.moves move = TypeOfMoves.moves.RIGHT;
                addMoves(move, rook_position, king_position);
            }
            else{
//                right side of king
                TypeOfMoves.moves move = TypeOfMoves.moves.LEFT;
                addMoves(move, rook_position, king_position);
            }
        }
        else {
//            same column
            if (king_position.getRow() > rook_position.getRow()){
                TypeOfMoves.moves move = TypeOfMoves.moves.UP;
                // bottom side of king
                addMoves(move, rook_position, king_position);
            }
            else {
//                upper side of king
                TypeOfMoves.moves move = TypeOfMoves.moves.DOWN;
                addMoves(move, rook_position, king_position);
            }
        }
    }

    private void queenAttack(ChessPosition queen_position, ChessPosition king_position){
        if (king_position.getRow() == queen_position.getRow() || king_position.getColumn() == queen_position.getColumn()){
//            use rook moves
            rookAttack(queen_position, king_position);
        }
        else {
//            use bishop moves
            bishopAttack(queen_position, king_position);

        }
    }
    private void pawnAttach(ChessPosition pawn_position, ChessPosition king_position){

    }
    public void knightAttach(ChessPosition knight_position, ChessPosition king_position){

    }
}
