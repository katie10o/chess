package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalc implements PieceMoveCalc {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ArrayList<ChessMove> moves;
    private final ChessPiece piece;

    public BishopMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        piece = board.getPiece(position);
        moves = new ArrayList<>();
        validateMoves();
    }

    @Override
    public void validateMoves() {
    ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.BISHOP);
    for (TypeOfMoves.moves move : possibleMoves) {
        ChessPosition new_pos = move.movePositions(position);
        while (board.extendsBoard(new_pos)){
            ChessPiece new_pos_type = board.getPiece(new_pos);
            if (new_pos_type != null && new_pos_type.getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(position, new_pos, null));
                break;
            }
            else if (new_pos_type != null && new_pos_type.getTeamColor() == piece.getTeamColor()) {
                break;
            }
            else if (new_pos_type == null){
                moves.add(new ChessMove(position, new_pos, null));
            }
            new_pos = move.movePositions(new_pos);
        }
    }
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
