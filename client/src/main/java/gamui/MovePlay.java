package gamui;

import chess.*;

import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class MovePlay {
    private ChessGame game;
    private ChessPosition oldPosition;
    private ChessPosition newPosition;
    private ChessGame.TeamColor teamColor;
    private String outcome;
    private ChessMove move;

    public MovePlay(ChessGame game, ChessPosition oldPosition, ChessPosition newPosition, ChessGame.TeamColor teamColor) {
        this.game = game;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.teamColor = teamColor;
        }
    public ChessMove getMove(){
        return move;
    }
    public String getOutcome(){
        return outcome;
    }

    public boolean safeMove(){
        try{
            if (gameResigned()) {
                outcome = "Game over, previous player resigned";
                return false;
            }
            if (checkMate(ChessGame.TeamColor.WHITE) || checkMate(ChessGame.TeamColor.BLACK)){
                outcome = "Game over, king in checkmate";
                return false;
            }
            if (staleMate(ChessGame.TeamColor.WHITE) || staleMate(ChessGame.TeamColor.BLACK)){
                outcome = "Game tied, king in stalemate";
                return false;
            }
            if (!game.getTeamTurn().equals(teamColor)){
                outcome = "not your turn to go";
                return false;
            }

            ChessPiece currPiece = game.getBoard().getPiece(oldPosition);
            ChessMove newMove;

            if (!currPiece.getTeamColor().equals(teamColor)){
                outcome = "cannot move piece that is not yours";
                return false;
            }

            if (checkPawnPromotion(currPiece)){ newMove = pawnPromotion(); }
            else { newMove = new ChessMove(oldPosition, newPosition, null); }

            if (validMoveChecker(newMove)) {
                try{
                    game.makeMove(newMove);
                    this.move = newMove;
                    return true;
                } catch (InvalidMoveException ex){
                    outcome = ex.getMessage();
                    return false;
                }
            } else {
                outcome = "Cannot make that move, try again";
                return false;
            }

        } catch (Exception ex){
            outcome = "Error making move";
            return false;
        }
    }

    private boolean checkPawnPromotion(ChessPiece currPiece){
        return ((newPosition.getRow() == 1 && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK &&
                currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor)) ||
                (newPosition.getRow() == 8 && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) &&
                        currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN) && currPiece.getTeamColor().equals(teamColor));
    }

    private ChessMove pawnPromotion(){
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println(">>> You selected a pawn, chose an upgrade: ");
            System.out.println("\t1. Queen\n\t2. Bishop\n\t3. Knight\n\t4. Rook");
            System.out.print(RESET_TEXT_COLOR + "\n>>> ");

            if (scanner.hasNext()){
                int choice = scanner.nextInt();
                switch (choice){
                    case 1 -> {return new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.QUEEN); }
                    case 2 -> {return new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.BISHOP);}
                    case 3 -> {return new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.KNIGHT);}
                    case 4 -> {return new ChessMove(oldPosition, newPosition, ChessPiece.PieceType.ROOK);}
                    default -> System.out.println("Invalid choice, choose between 1 through 4");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    private boolean validMoveChecker(ChessMove newMove){
        Collection<ChessMove> moves = game.validMoves(newMove.getStartPosition());
        for (ChessMove move : moves){
            if (move.equals(newMove)){ return true; }
        }
        return false;
    }

    private boolean checkMate(ChessGame.TeamColor color){
        return game.isInCheckmate(color);
    }
    private boolean staleMate(ChessGame.TeamColor color){
        return game.isInStalemate(color);
    }
    private boolean gameResigned() {
        return game.getResigned();
    }

}