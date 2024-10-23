package chess;

import InvalidMoveException;

import java.util.*;


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
    private boolean checkKingStatus(ChessGame.TeamColor color){
        if (board.getKingPosition(color) == null){
            return true;
        }
        ChessPosition kingPos = board.getKingPosition(color);
        KingSafetyChecker safetyChecker = new KingSafetyChecker(board, color);
        return safetyChecker.dangerChecker(kingPos);

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
            if (checkKingStatus(color)){
                safeMoves.add(move);
            }
            board.removePiece(future, piece);
            board.addPiece(future, pieceToRemove);
            board.addPiece(current, piece);
        }
        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = board.getPiece(move.getStartPosition());
        ChessPiece enemyPiece = board.getPiece(move.getEndPosition());


        if (myPiece == null){
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

            board.removePiece(move.getStartPosition(), myPiece);
            board.removePiece(move.getEndPosition(), enemyPiece);
            board.addPiece(move.getEndPosition(), promo);

            if (currColor == TeamColor.WHITE){
                setTeamTurn(TeamColor.BLACK);
            }
        }
        else {
            board.removePiece(move.getStartPosition(), myPiece);
            board.removePiece(move.getEndPosition(), enemyPiece);
            board.addPiece(move.getEndPosition(), myPiece);
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
        boolean kingSafe = checkKingStatus(teamColor);
        if (kingSafe){
            return check.get(teamColor);
        }
        runChecks(teamColor);
        return check.get(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean kingSafe = checkKingStatus(teamColor);
        if (kingSafe){
            return checkMate.get(teamColor);
        }
        runChecks(teamColor);
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
        ChessBoard startingBoard = new ChessBoard();
        startingBoard.resetBoard();
        if (board.equals(startingBoard)){
            return staleMate.get(teamColor);
        }
        runChecks(teamColor);
        return staleMate.get(teamColor);
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

    private void runChecks(ChessGame.TeamColor teamColor){
        boolean kingSafe = checkKingStatus(teamColor);
        ArrayList<ChessMove> allValidMoves = new ArrayList<>();
        ArrayList<ChessPosition> startingPositions = new ArrayList<>();
        HashMap<ChessPiece.PieceType, ArrayList<ChessPosition>> allPieces = board.getTeamPieces(teamColor);
        for (Map.Entry<ChessPiece.PieceType, ArrayList<ChessPosition>> entry : allPieces.entrySet()){
            ArrayList<ChessPosition> positions = entry.getValue();
            startingPositions.addAll(positions);
        }
        for (ChessPosition pos : startingPositions){
            Collection<ChessMove> positionValidMoves = validMoves(pos);
            allValidMoves.addAll(positionValidMoves);
        }
        if (allValidMoves.isEmpty() && !kingSafe){
            checkMate.put(teamColor, true);
            check.put(teamColor, true);
        }
        if (!allValidMoves.isEmpty() && !kingSafe){
            check.put(teamColor, true);
        }
        if (allValidMoves.isEmpty() && kingSafe){
            staleMate.put(teamColor, true);
        }
    }


}
