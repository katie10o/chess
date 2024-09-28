package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalc implements PieceMoveCalc{
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;
    private final ArrayList<ChessMove> moves;

    public RookMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
        this.moves = new ArrayList<>();
        validateMoves();
    }

    @Override
    public void validateMoves() {
        ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.ROOK);
        for (TypeOfMoves.moves move : possibleMoves) {
            ChessPosition new_position = move.movePositions(position);
            while (board.extendsBoard(new_position)) {
                if (board.getPiece(new_position) != null) {
                    if (board.getPiece(new_position).getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, new_position, null));
                    }
                    break;
                }
                moves.add(new ChessMove(position, new_position, null));
                new_position = move.movePositions(new_position);
            }

        }

    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
