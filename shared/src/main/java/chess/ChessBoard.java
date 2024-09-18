package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[9][9];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    /**
     * KTS personally created function
     * @param position a new position that a chess piece could go
     * @return bool to see if that new position will be out of the chess board
     */
    public boolean extendsBoard(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return row >= 1 && row < 9 && column >= 1 && column < 9;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        return "Have not implemented to string method on chessBoard yet...";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard chessBoard = (ChessBoard) o;
        return Arrays.deepEquals(board, chessBoard.board);
    }

    @Override
    public int hashCode() {
        return 71 * Arrays.deepHashCode(board);
    }
}
