package chess;

import java.util.Collection;
import java.util.Map;

public class SafetyChecker {
    boolean kingSafe = true;
    ChessBoard board;
    ChessPosition kingPos;
    ChessGame.TeamColor color;

    public SafetyChecker(ChessBoard board, ChessPosition kingPos, ChessGame.TeamColor color) {
        this.board = board;
        this.kingPos = kingPos;
        this.color = color;
    }

    private boolean pawnCheck(ChessPosition pawn){
        if ( !board.extendsBoard(pawn) || board.getPiece(pawn) == null){
            return false;
        }

        return board.getPiece(pawn).getPieceType() == ChessPiece.PieceType.PAWN
                && board.getPiece(pawn).getTeamColor() != color;
    }
    public void dangerChecker(){

        Map<ChessPiece.PieceType, Collection<ChessMove>> typesAndPossibleMoves = Map.of(
                ChessPiece.PieceType.KNIGHT, new KnightMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.BISHOP, new BishopMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.QUEEN, new QueenMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.ROOK, new RookMovesCalc(board, kingPos).getPossibleMoves(),
                ChessPiece.PieceType.KING, new KingMovesCalc(board, kingPos).getPossibleMoves()
        );
        for (Map.Entry<ChessPiece.PieceType, Collection<ChessMove>> entry : typesAndPossibleMoves.entrySet()){
            ChessPiece.PieceType type = entry.getKey();
            Collection<ChessMove> moves = entry.getValue();
            for (ChessMove move : moves){
                ChessPiece enemy_piece = board.getPiece(move.getEndPosition());
                if (enemy_piece != null && enemy_piece.getPieceType() == type && enemy_piece.getTeamColor() != color){
                    kingSafe = false;
                }
            }
        }
//        pawn check
        if (color == ChessGame.TeamColor.WHITE){
            ChessPosition pawnLeft = new ChessPosition(kingPos.getRow()+1, kingPos.getColumn()-1) ;
            ChessPosition pawnRight = new ChessPosition(kingPos.getRow()+1, kingPos.getColumn()+1);
            if (pawnCheck(pawnRight)){
                kingSafe = false;
            }
            else if (pawnCheck(pawnLeft)) {
                kingSafe = false;
            }
        }
        else if (color == ChessGame.TeamColor.BLACK){
            ChessPosition pawnLeft = new ChessPosition(kingPos.getRow()-1, kingPos.getColumn()-1);
            ChessPosition pawnRight = new ChessPosition(kingPos.getRow()-1, kingPos.getColumn()+1);
            if (pawnCheck(pawnRight)){
                kingSafe = false;
            }
            else if (pawnCheck(pawnLeft)) {
                kingSafe = false;
            }
        }
    }

    public boolean kingCheck(){
        return kingSafe;
    }

}
