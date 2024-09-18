package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalc implements PieceMoveCalc {
    private ChessBoard board;
    private ChessPosition position;
    private ArrayList<ChessMove> moves;
    private ChessPiece piece;


    @Override
    public void PieceMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        piece = board.getPiece(position);

    }


    public void validateMoves(){
//        need to implement a danger checker
        ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.KING);
        for (TypeOfMoves.moves move : possibleMoves) {
            ChessPosition new_position = move.movePositions(position);



            if (board.extendsBoard(new_position)){
                if (board.getPiece(new_position) == null){
                    this.moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));
                }
//                else if (board.getPiece(new_position) == )

            }
            if (board.getPiece(new_position) == null && board.extendsBoard(new_position)) {
                this.moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));
            }

            this.moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KING));


        }
    }
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }

}
