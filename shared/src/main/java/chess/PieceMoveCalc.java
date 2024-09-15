package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMoveCalc {

    Collection<ChessMove> PieceMoves(ChessBoard board, ChessPosition position);
    void validateMoves();

}
