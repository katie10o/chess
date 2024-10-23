package chess.piece_moves_calc;

import chess.ChessPiece;
import chess.ChessPosition;

import java.util.*;

public class TypeOfMoves {
    private static final Map<ChessPiece.PieceType, List<Moves>> PIECE_WITH_MOVES = Map.of(
            ChessPiece.PieceType.KING, List.of(
                    Moves.UP,
                    Moves.DOWN,
                    Moves.LEFT,
                    Moves.RIGHT,
                    Moves.DIAGONAL_LEFT_UP,
                    Moves.DIAGONAL_LEFT_DOWN,
                    Moves.DIAGONAL_RIGHT_UP,
                    Moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.QUEEN,  List.of(
                    Moves.UP,
                    Moves.DOWN,
                    Moves.LEFT,
                    Moves.RIGHT,
                    Moves.DIAGONAL_LEFT_UP,
                    Moves.DIAGONAL_LEFT_DOWN,
                    Moves.DIAGONAL_RIGHT_UP,
                    Moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.KNIGHT,  List.of(
                    Moves.UP_TWO_RIGHT_ONE,
                    Moves.UP_TWO_LEFT_ONE,
                    Moves.DOWN_TWO_RIGHT_ONE,
                    Moves.DOWN_TWO_LEFT_ONE,
                    Moves.UP_ONE_RIGHT_TWO,
                    Moves.UP_ONE_LEFT_TWO,
                    Moves.DOWN_ONE_RIGHT_TWO,
                    Moves.DOWN_ONE_LEFT_TWO
            ),
            ChessPiece.PieceType.BISHOP,  List.of(
                    Moves.DIAGONAL_LEFT_UP,
                    Moves.DIAGONAL_LEFT_DOWN,
                    Moves.DIAGONAL_RIGHT_UP,
                    Moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.ROOK,  List.of(
                    Moves.UP,
                    Moves.DOWN,
                    Moves.LEFT,
                    Moves.RIGHT
            ),
            ChessPiece.PieceType.PAWN,  List.of(
                    Moves.UP,
                    Moves.DIAGONAL_RIGHT_UP,
                    Moves.DIAGONAL_LEFT_UP,
                    Moves.DOWN,
                    Moves.DIAGONAL_LEFT_DOWN,
                    Moves.DIAGONAL_RIGHT_DOWN
            )
            );

    public enum Moves {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        DIAGONAL_LEFT_UP,
        DIAGONAL_LEFT_DOWN,
        DIAGONAL_RIGHT_UP,
        DIAGONAL_RIGHT_DOWN,
        UP_TWO_RIGHT_ONE,
        UP_TWO_LEFT_ONE,
        DOWN_TWO_RIGHT_ONE,
        DOWN_TWO_LEFT_ONE,
        UP_ONE_RIGHT_TWO,
        UP_ONE_LEFT_TWO,
        DOWN_ONE_RIGHT_TWO,
        DOWN_ONE_LEFT_TWO;

        public ChessPosition movePositions(ChessPosition pos) {
            return switch (this) {
                case UP -> new ChessPosition(pos.getRow() + 1, pos.getColumn());
                case DOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn());
                case LEFT -> new ChessPosition(pos.getRow(), pos.getColumn() - 1);
                case RIGHT -> new ChessPosition(pos.getRow(), pos.getColumn() + 1);
                case DIAGONAL_LEFT_UP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() - 1);
                case DIAGONAL_LEFT_DOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() - 1);
                case DIAGONAL_RIGHT_UP -> new ChessPosition(pos.getRow() + 1, pos.getColumn() + 1);
                case DIAGONAL_RIGHT_DOWN -> new ChessPosition(pos.getRow() - 1, pos.getColumn() + 1);
                case UP_TWO_RIGHT_ONE -> new ChessPosition(pos.getRow() + 2, pos.getColumn() + 1);
                case UP_TWO_LEFT_ONE -> new ChessPosition(pos.getRow() + 2, pos.getColumn() - 1);
                case DOWN_TWO_RIGHT_ONE -> new ChessPosition(pos.getRow() - 2, pos.getColumn() + 1);
                case DOWN_TWO_LEFT_ONE -> new ChessPosition(pos.getRow() - 2, pos.getColumn() - 1);
                case UP_ONE_RIGHT_TWO -> new ChessPosition(pos.getRow() + 1, pos.getColumn() + 2);
                case UP_ONE_LEFT_TWO -> new ChessPosition(pos.getRow() + 1, pos.getColumn() - 2);
                case DOWN_ONE_RIGHT_TWO -> new ChessPosition(pos.getRow() - 1, pos.getColumn() + 2);
                case DOWN_ONE_LEFT_TWO -> new ChessPosition(pos.getRow() - 1, pos.getColumn() - 2);
            };
        }
    }

    public static List<Moves> getPieceWithMoves(ChessPiece.PieceType type) {
        return PIECE_WITH_MOVES.get(type);
    }

}
