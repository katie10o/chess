package chess.calc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalc extends PieceMoveCalc {
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

    private void addPromotionPiece(ChessPosition newPosition){
        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
    }

    private void validateHelper(TypeOfMoves.Moves currMove, TypeOfMoves.Moves upOrDown, ChessPosition newPosition,
                                int starterRow, int endOfBoard){

        if (currMove == upOrDown){
            if (board.getPiece(newPosition) == null){
                if (newPosition.getRow() == endOfBoard) {
                    addPromotionPiece(newPosition);
                    return;
                }
                moves.add(new ChessMove(position, newPosition, null));
                if (position.getRow() == starterRow){
                    newPosition = currMove.movePositions(newPosition);
                    if (board.getPiece(newPosition) == null){
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                }
            }
        }
        else{
            if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                if (newPosition.getRow() == endOfBoard) {
                    addPromotionPiece(newPosition);
                    return;
                }
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
    }

    @Override
    public void validateMoves() {
        List<TypeOfMoves.Moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.PAWN);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            possibleMoves = new ArrayList<>(possibleMoves.subList(0,3));
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            possibleMoves = new ArrayList<>(possibleMoves.subList(3, possibleMoves.size()));        }

        for (TypeOfMoves.Moves move : possibleMoves) {
            ChessPosition newPosition = move.movePositions(position);
            if (board.insideBoard(newPosition)){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    validateHelper(move, TypeOfMoves.Moves.UP, newPosition, 2, 8);
                }
                else{
                    validateHelper(move, TypeOfMoves.Moves.DOWN, newPosition, 7, 1);
                }
            }
        }
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
