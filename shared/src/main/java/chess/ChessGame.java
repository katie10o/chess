package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


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


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */



    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
//        possibleMoves.add(new ChessMove(startPosition, startPosition, null));


        SafetyChecker safetyChecker;

        ArrayList<ChessMove> safeMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves){
            ChessPiece removedPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.removePiece(move.getStartPosition());
//            System.out.println(board.toString());
            ChessPosition kingPos = board.getKingPosition(piece.getTeamColor());
            safetyChecker = new SafetyChecker(board, kingPos, color);
            safetyChecker.dangerChecker();
            boolean kingStatus = safetyChecker.kingCheck();
            if (kingStatus){
                safeMoves.add(move);
            }
            board.addPiece(move.getEndPosition(), removedPiece);
        }
        board.addPiece(startPosition, piece);
//        System.out.println(board.toString());
        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> dangerMoves = validMoves(move.getStartPosition());
        if (!dangerMoves.isEmpty() && !dangerMoves.contains(move.getEndPosition())){
            throw new InvalidMoveException("something wrong in makeMove");
        }

        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
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
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
