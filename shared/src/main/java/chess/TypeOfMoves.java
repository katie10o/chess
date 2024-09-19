package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TypeOfMoves {
    private static HashMap<ChessPiece.PieceType, ArrayList<moves>> pieceWithMoves = new HashMap<>();

    static {
        pieceWithMoves = new HashMap<>()  {{
            put(ChessPiece.PieceType.KING, new ArrayList<>(Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            )));
            put(ChessPiece.PieceType.QUEEN, new ArrayList<>(Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            )));
            put(ChessPiece.PieceType.KNIGHT, new ArrayList<>(Arrays.asList(
                    moves.UP_TWO_RIGHT_ONE,
                    moves.UP_TWO_LEFT_ONE,
                    moves.DOWN_TWO_RIGHT_ONE,
                    moves.DOWN_TWO_LEFT_ONE,
                    moves.UP_ONE_RIGHT_TWO,
                    moves.UP_ONE_LEFT_TWO,
                    moves.DOWN_ONE_RIGHT_TWO,
                    moves.DOWN_ONE_LEFT_TWO
            )));
            put(ChessPiece.PieceType.BISHOP, new ArrayList<>(Arrays.asList(
                    moves.DIAGONAL_LEFT_UP,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_RIGHT_DOWN
            )));
            put(ChessPiece.PieceType.ROOK, new ArrayList<>(Arrays.asList(
                    moves.UP,
                    moves.DOWN,
                    moves.LEFT,
                    moves.RIGHT
            )));
            put(ChessPiece.PieceType.PAWN, new ArrayList<>(Arrays.asList(
                    moves.UP,
                    moves.DIAGONAL_RIGHT_UP,
                    moves.DIAGONAL_LEFT_UP,
                    moves.DOWN,
                    moves.DIAGONAL_LEFT_DOWN,
                    moves.DIAGONAL_RIGHT_DOWN
            )));
        }};

    }

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

    public static ArrayList<moves> getPieceWithMoves(ChessPiece.PieceType type) {
        return pieceWithMoves.get(type);
    }
}
