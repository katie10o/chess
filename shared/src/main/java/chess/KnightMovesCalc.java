package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalc implements PieceMoveCalc {
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ArrayList<ChessMove> moves;

    public KnightMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        piece = board.getPiece(position);
        validateMoves();

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
