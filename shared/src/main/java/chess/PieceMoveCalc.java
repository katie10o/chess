package chess;

import java.util.Collection;

public interface PieceMoveCalc {
    void validateMoves();
    Collection<ChessMove> getPossibleMoves();
}
