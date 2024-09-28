package chess;

import java.util.*;

public class TypeOfMoves {
    private static final Map<ChessPiece.PieceType, List<moves>> pieceWithMoves = Map.of(
            ChessPiece.PieceType.KING, Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.QUEEN, Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.KNIGHT, Arrays.asList(
                    moves.UP_TWO_RIGHT_ONE,
                    moves.UP_TWO_LEFT_ONE,
                    moves.DOWN_TWO_RIGHT_ONE,
                    moves.DOWN_TWO_LEFT_ONE,
                    moves.UP_ONE_RIGHT_TWO,
                    moves.UP_ONE_LEFT_TWO,
                    moves.DOWN_ONE_RIGHT_TWO,
                    moves.DOWN_ONE_LEFT_TWO
            ),
            ChessPiece.PieceType.BISHOP, Arrays.asList(
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            ),
            ChessPiece.PieceType.ROOK, Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT
            ),
            ChessPiece.PieceType.PAWN, Arrays.asList(
                    moves.UP,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DOWN,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_DOWN
            )
            );

    public enum moves {
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

    public static List<moves> getPieceWithMoves(ChessPiece.PieceType type) {
        return pieceWithMoves.get(type);
    }

}
