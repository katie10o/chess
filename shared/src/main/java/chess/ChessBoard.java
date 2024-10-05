package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[9][9];
    private HashMap<ChessPiece, ChessPosition> kingLocations = new HashMap<>();
    private final HashMap<ChessPiece, ArrayList<ChessPosition>> startingPositions = new HashMap<>() {{
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING)), new ArrayList<>(List.of(
                    new ChessPosition(1, 5)
            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING)), new ArrayList<>(List.of(
                    new ChessPosition(8, 5)
            )));
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN)), new ArrayList<>(List.of(
                    new ChessPosition(1, 4)
            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN)), new ArrayList<>(List.of(
                    new ChessPosition(8, 4)
            )));
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP)), new ArrayList<>(List.of(
                    new ChessPosition(1, 3),
                    new ChessPosition(1, 6)
            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP)), new ArrayList<>(List.of(
                    new ChessPosition(8, 3),
                    new ChessPosition(8, 6)
            )));
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT)), new ArrayList<>(List.of(
                    new ChessPosition(1, 2),
                    new ChessPosition(1, 7)
            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT)), new ArrayList<>(List.of(
                    new ChessPosition(8, 2),
                    new ChessPosition(8, 7)
            )));
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)), new ArrayList<>(List.of(
                    new ChessPosition(1, 1),
                    new ChessPosition(1, 8)
            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)), new ArrayList<>(List.of(
                    new ChessPosition(8, 1),
                    new ChessPosition(8, 8)
            )));
            put((new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)), new ArrayList<>(List.of(
                    new ChessPosition(2, 1),
                    new ChessPosition(2, 2),
                    new ChessPosition(2, 3),
                    new ChessPosition(2, 4),
                    new ChessPosition(2, 5),
                    new ChessPosition(2, 6),
                    new ChessPosition(2, 7),
                    new ChessPosition(2, 8)

            )));
            put((new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)), new ArrayList<>(List.of(
                    new ChessPosition(7, 1),
                    new ChessPosition(7, 2),
                    new ChessPosition(7, 3),
                    new ChessPosition(7, 4),
                    new ChessPosition(7, 5),
                    new ChessPosition(7, 6),
                    new ChessPosition(7, 7),
                    new ChessPosition(7, 8)
            )));


        }};


    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING){
            kingLocations.put(piece, position);
        }
        board[position.getRow()][position.getColumn()] = piece;
    }
    public void removePiece(ChessPosition position){
        board[position.getRow()][position.getColumn()] = null;
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
    public boolean insideBoard(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return row >= 1 && row < 9 && column >= 1 && column < 9;
    }
    public ChessPosition getKingPosition(ChessGame.TeamColor color){
        for (Map.Entry<ChessPiece, ChessPosition> entry : kingLocations.entrySet()){
            ChessPiece piece = entry.getKey();
            ChessPosition pos = entry.getValue();
            if (piece.getTeamColor() == color){
                return pos;
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[9][9];
        for (Map.Entry<ChessPiece, ArrayList<ChessPosition>> entry : startingPositions.entrySet() ){
            ChessPiece piece = entry.getKey();
            ArrayList<ChessPosition> positions = entry.getValue();
            for (ChessPosition position : positions) {
                addPiece(position, piece);
            }
        }
    }



    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = board.length -1; i >= 1; i--){
            boardString.append("|");
            for (int j = 1; j <= board[i].length -1; j++) {
                ChessPiece piece = board[i][j];
                if (piece == null) {
                    boardString.append(" |");
                }
                else {
                    boardString.append(String.format("%s|", piece));
                }
            }
                boardString.append("\n");
        }
        return boardString.toString();
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
