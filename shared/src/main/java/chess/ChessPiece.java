package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {


    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {

        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (this.type) {
            case BISHOP -> new BishopMovesCalc(board, myPosition).getPossibleMoves();
            case KNIGHT -> new KnightMovesCalc(board,myPosition).getPossibleMoves();
            case QUEEN -> new QueenMovesCalc(board,myPosition).getPossibleMoves();
            case ROOK -> new RookMovesCalc(board,myPosition).getPossibleMoves();
            case PAWN -> new PawnMovesCalc(board,myPosition).getPossibleMoves();
            case KING -> new KingMovesCalc(board,myPosition).getPossibleMoves();
        };
    }
    public String toString() {
        char result;
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            if (this.type == PieceType.KNIGHT){
                return "n";
            }
            result = this.type.toString().toLowerCase().charAt(0);
        }
        else  {
            if (this.type == PieceType.KNIGHT){
                return "N";
            }
            result = this.type.toString().toUpperCase().charAt(0);
        }
        return Character.toString(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return 71 * (37 * pieceColor.hashCode() + 89 * type.hashCode());
    }
}
