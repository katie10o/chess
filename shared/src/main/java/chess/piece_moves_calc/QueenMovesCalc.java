package chess.piece_moves_calc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalc extends PieceMoveCalc{
    private final ChessBoard board;
    private final ChessPosition position;
    private final ArrayList<ChessMove> moves;

    public QueenMovesCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        validateMoves();

    }

    @Override
    public void validateMoves() {
        addMoves(ChessPiece.PieceType.QUEEN, board, position, moves);
        }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}



