package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalc implements PieceMoveCalc {
    private ChessBoard board;
    private ChessPiece piece;
    private ChessPosition position;
    private ArrayList<ChessMove> moves;
    @Override
    public void PieceMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        piece = board.getPiece(position);

    }

    @Override
    public void validateMoves() {
        ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.KNIGHT);
        for (TypeOfMoves.moves move : possibleMoves) {
            ChessPosition new_position = move.movePositions(position);
            if (!board.extendsBoard(new_position)) {
                 continue;
            }
            if (board.getPiece(new_position) != null && (piece.getTeamColor() != board.getPiece(new_position).getTeamColor())) {
                moves.add(new ChessMove(position, new_position, null));
            }
            else if (board.getPiece(new_position) == null) {
                moves.add(new ChessMove(position, new_position, null));
            }
        }

    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
