package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.*;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private Boolean check = false;
    private Boolean checkMate = false;
    private Boolean staleMate = false;
    private ChessBoard board = null;
    private ArrayList<ChessPosition> dangerPositions = new ArrayList<>();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    
    


    private ArrayList<ChessPosition> kingSafety(ChessPiece currPiece){
        ChessPosition kingPos = board.getKingPosition(currPiece.getTeamColor());
        AttackMoves attackMoves = new AttackMoves();

        Map<ChessPiece.PieceType, Collection<ChessMove>> typesAndPossibleMoves = Map.of(
                ChessPiece.PieceType.KNIGHT, new KnightMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.PAWN, new PawnMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.BISHOP, new BishopMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.QUEEN, new QueenMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.ROOK, new RookMovesCalc(board, kingPos).getPossibleMoves()
                );

        for (Map.Entry<ChessPiece.PieceType, Collection<ChessMove>> entry : typesAndPossibleMoves.entrySet()){
            ChessPiece.PieceType type = entry.getKey();
            Collection<ChessMove> moves = entry.getValue();
            for (ChessMove move : moves){
                ChessPiece new_piece = board.getPiece(move.getEndPosition());
                if (new_piece != null && new_piece.getPieceType() == type){
                    if (type == ChessPiece.PieceType.KNIGHT){
                        attackMoves.knightAttach(move.getEndPosition(), kingPos);
                    }
                   else {
                       attackMoves.validAttackMoves(type, kingPos, move.getEndPosition());
                    }
                }
            }
        }

        
        ArrayList<ChessPosition> temp = (ArrayList<ChessPosition>) dangerPositions.clone();
        dangerPositions.clear();
        return temp;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */


    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        ArrayList<ChessPosition> dangerPositions = kingSafety(piece);

        ArrayList<ChessMove> tempList = new ArrayList<>();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : possibleMoves){
            ChessPosition new_position = move.getEndPosition();
            if (dangerPositions.isEmpty()){
                tempList.add(move);
            }
            else if (dangerPositions.contains(new_position)){
                tempList.add(move);
            }
        }

        return tempList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        return checkMate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        return staleMate;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        System.out.println(board.toString());
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        throw new RuntimeException("Not implemented");
    }
}
