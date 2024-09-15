package chess;

import java.util.Collection;

public interface PieceMoveCalc {

    void PieceMoves(ChessBoard board, ChessPosition position);
    void validateMoves();
    Collection<ChessMove> getPossibleMoves();

}
