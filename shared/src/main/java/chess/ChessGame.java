package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private Map<ChessGame.TeamColor, Boolean> check = new HashMap<>();
    private Map<ChessGame.TeamColor, Boolean> checkMate = new HashMap<>();
    private Map<ChessGame.TeamColor, Boolean> staleMate = new HashMap<>();
    private ChessBoard board;
    private ChessGame.TeamColor teamTurn;
    private boolean kingSafe = true;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        check.put(TeamColor.WHITE, false);
        check.put(TeamColor.BLACK, false);
        checkMate.put(TeamColor.WHITE, false);
        checkMate.put(TeamColor.BLACK, false);
        staleMate.put(TeamColor.WHITE, false);
        staleMate.put(TeamColor.BLACK, false);

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;

    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    private void checkKingStatus(ChessGame.TeamColor color){
        if (board.getKingPosition(color) == null){
            return;
        }
        System.out.println(board.toString());

        ChessPosition kingPos = board.getKingPosition(color);
        SafetyChecker safetyChecker = new SafetyChecker(board, color);
        kingSafe = safetyChecker.dangerChecker(kingPos);



//        Collection<ChessMove> kingValidMoves = validMoves(board.getKingPosition(color));
//
//        if (kingValidMoves.isEmpty() && !kingStatus){
//            checkMate.get(color);
//        }
//        else if (kingValidMoves.isEmpty()){
//            staleMate.get(color);
//        }
//        else if (!kingStatus){
//            check.get(color);
//        }

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

        ArrayList<ChessMove> safeMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves){
            ChessPosition current = move.getStartPosition();
            ChessPosition future = move.getEndPosition();
            ChessPiece pieceToRemove = board.getPiece(move.getEndPosition());

            board.removePiece(current, piece);
            board.removePiece(future, pieceToRemove);

            board.addPiece(future, piece);
            System.out.println(board.toString());

            checkKingStatus(color);
            if (kingSafe){
                safeMoves.add(move);
            }
            board.removePiece(future, piece);
            board.addPiece(future, pieceToRemove);
            board.addPiece(current, piece);
        }
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
        if (board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("cannot move null piece");
        }

        Collection<ChessMove> safeMoves = validMoves(move.getStartPosition());
        ChessGame.TeamColor currColor = board.getPiece(move.getStartPosition()).getTeamColor();

        if (!board.insideBoard(move.getEndPosition())){
            throw new InvalidMoveException("Move outside of chess board");
        }
        else if (!safeMoves.isEmpty() && !safeMoves.contains(move)){
            throw new InvalidMoveException("Cannot make move, puts king in danger");
        }
        else if (safeMoves.isEmpty()){
            throw new InvalidMoveException("No save moves available");
        }
        else if (this.teamTurn != currColor){
            throw new InvalidMoveException("Not teams color");
        }

        if (move.getPromotionPiece() != null){
            ChessPiece promo = new ChessPiece(currColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promo);
            board.removePiece(move.getStartPosition(), board.getPiece(move.getStartPosition()));
            if (currColor == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }
        }
        else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition(), board.getPiece(move.getStartPosition()));
        }
        checkKingStatus(currColor);

        if (currColor == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
        System.out.println(board.toString());
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return check.get(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return checkMate.get(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return staleMate.get(teamColor);

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        checkKingStatus(TeamColor.WHITE);
        checkKingStatus(TeamColor.BLACK);
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
