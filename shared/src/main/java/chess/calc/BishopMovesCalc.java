package chess.calc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalc extends PieceMoveCalc {
    private final ChessBoard board;
    private final ChessPosition position;
    private final ArrayList<ChessMove> moves;

    public BishopMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        validateMoves();
    }

    @Override
    public void validateMoves() {
        addMoves(ChessPiece.PieceType.BISHOP, board, position, moves);

    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
