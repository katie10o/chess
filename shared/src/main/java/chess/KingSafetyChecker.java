package chess;

import chess.PieceMovesCalc.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KingSafetyChecker {
    ChessBoard board;
    ChessGame.TeamColor color;

    public KingSafetyChecker(ChessBoard board, ChessGame.TeamColor color) {
        this.board = board;
        this.color = color;
    }

    private boolean pawnCheck(ChessPosition pawn){
        if ( !board.insideBoard(pawn) || board.getPiece(pawn) == null){
            return false;
        }

        return board.getPiece(pawn).getPieceType() == ChessPiece.PieceType.PAWN
                && board.getPiece(pawn).getTeamColor() != color;
    }

    private HashMap<ChessPiece.PieceType, Collection<ChessMove>> attackPositions(ChessPosition kingPos) {
        HashMap<ChessPiece.PieceType, Collection<ChessMove>> typesAndPossibleMoves = new HashMap<>();
        typesAndPossibleMoves.put(ChessPiece.PieceType.KNIGHT, new KnightMovesCalc(board, kingPos).getPossibleMoves());
        typesAndPossibleMoves.put(ChessPiece.PieceType.BISHOP, new BishopMovesCalc(board, kingPos).getPossibleMoves());
        typesAndPossibleMoves.put(ChessPiece.PieceType.QUEEN, new QueenMovesCalc(board, kingPos).getPossibleMoves());
        typesAndPossibleMoves.put(ChessPiece.PieceType.ROOK, new RookMovesCalc(board, kingPos).getPossibleMoves());
        typesAndPossibleMoves.put(ChessPiece.PieceType.KING, new KingMovesCalc(board, kingPos).getPossibleMoves());

        return typesAndPossibleMoves;
    }


    public boolean dangerChecker(ChessPosition kingPos){
        HashMap<ChessPiece.PieceType, Collection<ChessMove>> typesAndPossibleMoves = attackPositions(kingPos);
        for (Map.Entry<ChessPiece.PieceType, Collection<ChessMove>> entry : typesAndPossibleMoves.entrySet()){
            ChessPiece.PieceType type = entry.getKey();
            Collection<ChessMove> moves = entry.getValue();
            for (ChessMove move : moves){
                ChessPiece enemy_piece = board.getPiece(move.getEndPosition());
                if (enemy_piece != null && enemy_piece.getPieceType() == type && enemy_piece.getTeamColor() != color){
                    return false;
                }
            }
        }

//        pawn check
        if (color == ChessGame.TeamColor.WHITE){
            ChessPosition pawnLeft = new ChessPosition(kingPos.getRow()+1, kingPos.getColumn()-1) ;
            ChessPosition pawnRight = new ChessPosition(kingPos.getRow()+1, kingPos.getColumn()+1);
            if (pawnCheck(pawnRight)){
                return false;

            }
            else if (pawnCheck(pawnLeft)) {
                return false;

            }
        }
        else if (color == ChessGame.TeamColor.BLACK){
            ChessPosition pawnLeft = new ChessPosition(kingPos.getRow()-1, kingPos.getColumn()-1);
            ChessPosition pawnRight = new ChessPosition(kingPos.getRow()-1, kingPos.getColumn()+1);
            if (pawnCheck(pawnRight)){
                return false;

            }
            else if (pawnCheck(pawnLeft)) {
                return false;

            }
        }
        return true;
    }



}
