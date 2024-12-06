package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        var p = (promotionPiece == null ? "" : " Promotion: " + promotionPiece);
        return String.format("[%s] to [%s].%s", startPosition.toString(), endPosition.toString(), p);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessMove that = (ChessMove) o;
        boolean start = startPosition.equals(that.startPosition);
        boolean end = endPosition.equals(that.endPosition);
        boolean promotion = false;
        if (promotionPiece == null && that.promotionPiece == null) {
            promotion = true;
        }
        else if (promotionPiece != null && promotionPiece.equals(that.promotionPiece)) {
            promotion = true;
        }
        return start && end && promotion;
    }
    @Override
    public int hashCode() {
        return 71 *  startPosition.hashCode() +  endPosition.hashCode() + (promotionPiece == null ? 47 : promotionPiece.hashCode());
    }
}
