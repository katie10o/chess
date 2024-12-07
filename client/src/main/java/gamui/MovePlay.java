package gamui;

import chess.*;

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

    public ChessMove promoMoveChecker(){
        ChessPiece currPiece = game.getBoard().getPiece(oldPosition);
        ChessMove newMove;

        if (checkPawnPromotion(currPiece)){ newMove = pawnPromotion(); }
        else { newMove = new ChessMove(oldPosition, newPosition, null); }

        return newMove;
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
            System.out.println(">>> Pawn on the promotion square, chose an upgrade: ");
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

}