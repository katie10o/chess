package chess;

import java.util.ArrayList;
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
        switch (this.type) {
            case BISHOP:
                BishopMovesCalc bishop = new BishopMovesCalc();
                bishop.PieceMoves(board, myPosition);
                bishop.validateMoves();
                return bishop.getPossibleMoves();
            case KNIGHT:
                KnightMovesCalc knight = new KnightMovesCalc();
                knight.PieceMoves(board, myPosition);
                knight.validateMoves();
                return knight.getPossibleMoves();
            case QUEEN:
                QueenMovesCalc queen = new QueenMovesCalc();
                queen.PieceMoves(board, myPosition);
                queen.validateMoves();
                return queen.getPossibleMoves();
            case ROOK:
                RookMovesCalc rook = new RookMovesCalc();
                rook.PieceMoves(board, myPosition);
                rook.validateMoves();
                return rook.getPossibleMoves();
            case PAWN:
                PawnMovesCalc pawn = new PawnMovesCalc();
                pawn.PieceMoves(board, myPosition);
                pawn.validateMoves();
                return pawn.getPossibleMoves();
            case KING:
                KingMovesCalc king = new KingMovesCalc();
                king.PieceMoves(board, myPosition);
                king.validateMoves();
                return king.getPossibleMoves();

            default:
                System.out.println("Chess Piece class, switch case default");
                return new ArrayList<>();

        }
    }
    public String toString() {
        return "type: " + this.type.toString() + "color: " + this.pieceColor;
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
        return 71 * (pieceColor.hashCode() + type.hashCode());
    }
}
