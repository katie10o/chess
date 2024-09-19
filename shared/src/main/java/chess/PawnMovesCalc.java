package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalc implements PieceMoveCalc {
    private ChessBoard board;
    private ChessPiece piece;
    private ChessPosition position;
    private ArrayList<ChessMove> moves;
    @Override
    public void PieceMoves(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        moves = new ArrayList<>();
        piece = board.getPiece(position);

    }

    @Override
    public void validateMoves() {
        ArrayList<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(ChessPiece.PieceType.PAWN);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            possibleMoves = new ArrayList<>(possibleMoves.subList(0,3));
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            possibleMoves = new ArrayList<>(possibleMoves.subList(3, possibleMoves.size()));        }


        for (TypeOfMoves.moves move : possibleMoves) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    ChessPosition new_position = move.movePositions(position);
                    if (board.extendsBoard(new_position)){
                        if (move == TypeOfMoves.moves.UP){
                            if (board.getPiece(new_position) == null){
                                if (new_position.getRow() == 8){
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.BISHOP));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.ROOK));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.QUEEN));
                                }
                                else if (position.getRow() == 2){
                                    moves.add(new ChessMove(position, new_position, null));
                                    new_position = move.movePositions(new_position);
                                    if (board.getPiece(new_position) == null){
                                        moves.add(new ChessMove(position, new_position, null));
                                    }
                                }
                                else {
                                    moves.add(new ChessMove(position, new_position, null));
                                }
                            }
                        }
                        else {
                            if (board.getPiece(new_position) != null && board.getPiece(new_position).getTeamColor() != piece.getTeamColor()){
                                if (new_position.getRow() == 8){
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.BISHOP));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.ROOK));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KNIGHT));
                                    moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.QUEEN));
                                }
                                else {
                                    moves.add(new ChessMove(position, new_position, null));
                                }
                            }
                        }


                    }
                }
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                ChessPosition new_position = move.movePositions(position);
                if (board.extendsBoard(new_position)){
                    if (move == TypeOfMoves.moves.DOWN){
                        if (board.getPiece(new_position) == null){
                            if (new_position.getRow() == 1){
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.BISHOP));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.QUEEN));
                            }
                            else if (position.getRow() == 7){
                                moves.add(new ChessMove(position, new_position, null));
                                new_position = move.movePositions(new_position);
                                if (board.getPiece(new_position) == null){
                                    moves.add(new ChessMove(position, new_position, null));
                                }
                            }
                            else {
                                moves.add(new ChessMove(position, new_position, null));
                            }
                        }
                    }
                    else {
                        if (board.getPiece(new_position) != null && board.getPiece(new_position).getTeamColor() != piece.getTeamColor()){
                            if (new_position.getRow() == 1){
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.BISHOP));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.ROOK));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.KNIGHT));
                                moves.add(new ChessMove(position, new_position, ChessPiece.PieceType.QUEEN));
                            }
                            else {
                                moves.add(new ChessMove(position, new_position, null));
                            }
                        }
                    }


                }
            }
        }
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        return moves;
    }
}
