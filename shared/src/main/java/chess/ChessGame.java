package chess;

import java.util.ArrayList;
import java.util.Collection;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private Boolean checkWhite;
    private Boolean checkMateWhite;
    private Boolean staleMateWhite;
    private Boolean checkBlack;
    private Boolean checkMateBlack;
    private Boolean staleMateBlack;
    private ChessBoard board;
    private ChessGame.TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
        checkWhite = false;
        checkMateWhite = false;
        staleMateWhite = false;
        checkBlack = false;
        checkMateBlack = false;
        staleMateBlack = false;
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
        ChessPosition kingPos = board.getKingPosition(color);

        SafetyChecker safetyChecker = new SafetyChecker(board, kingPos, color);
        safetyChecker.dangerChecker();
        boolean kingStatus = safetyChecker.kingCheck();

        Collection<ChessMove> kingValidMoves = validMoves(board.getKingPosition(color));

        if (kingValidMoves.isEmpty() && !kingStatus){
            if (color == TeamColor.BLACK){
                checkMateBlack = true;
            }
            else{checkMateWhite = true;}
        }
        else if (kingValidMoves.isEmpty()){
            if (color == TeamColor.BLACK){
                staleMateBlack = true;
            }
            else{staleMateWhite = true;}
        }
        else if (!kingStatus){
            if (color == TeamColor.BLACK){
                checkBlack = true;
            }
            else{checkWhite = true;}
        }

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
            if (kingPos == null){
                safeMoves.add(move);
                continue;
            }
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
            board.removePiece(move.getStartPosition());
            if (currColor == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }
        }
        else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition());
        }
        checkKingStatus(currColor);

        if (currColor == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.BLACK){
            return checkBlack;
        }
        return checkWhite;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (teamColor == TeamColor.BLACK){
            return checkMateBlack;
        }
        return checkMateWhite;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor == TeamColor.BLACK){
            return staleMateBlack;
        }
        return staleMateWhite;    }

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
