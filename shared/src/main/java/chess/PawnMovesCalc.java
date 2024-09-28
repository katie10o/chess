package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalc implements PieceMoveCalc {
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ArrayList<ChessMove> moves;

    public PawnMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        piece = board.getPiece(position);
        validateMoves();
    }

    private void addPromotionPiece(ChessPosition new_position){
        moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.QUEEN));
    }

    private void validateHelper(TypeOfMoves.moves curr_move, TypeOfMoves.moves up_or_down, ChessPosition new_position,
                                int starterRow, int endOfBoard){

        if (curr_move == up_or_down){
            if (board.getPiece(new_position) == null){
                if (new_position.getRow() == endOfBoard) {
                    addPromotionPiece(new_position);
                    return;
                }
                moves.add(new ChessMove(position, new_position, null));
                if (position.getRow() == starterRow){
                    new_position = curr_move.movePositions(new_position);
                    if (board.getPiece(new_position) == null){
                        moves.add(new ChessMove(position, new_position, null));
                    }
                }
            }
        }
        else{
            if (board.getPiece(new_position) != null && board.getPiece(new_position).getTeamColor() != piece.getTeamColor()){
                if (new_position.getRow() == endOfBoard) {
                    addPromotionPiece(new_position);
                    return;
                }
                moves.add(new ChessMove(position, new_position, null));
            }
        }
    }

    @Override
    public void validateMoves() {
        ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.PAWN);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            possibleMoves = new ArrayList<>(possibleMoves.subList(0,3));
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            possibleMoves = new ArrayList<>(possibleMoves.subList(3, possibleMoves.size()));        }

        for (TypeOfMoves.moves move : possibleMoves) {
            ChessPosition new_position = move.movePositions(position);
            if (board.extendsBoard(new_position)){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    validateHelper(move, TypeOfMoves.moves.UP, new_position, 2, 8);
                }
                else{
                    validateHelper(move, TypeOfMoves.moves.DOWN, new_position, 7, 1);
                }
            }
        }
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
