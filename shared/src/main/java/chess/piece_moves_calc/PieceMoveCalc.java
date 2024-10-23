package chess.piece_moves_calc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class PieceMoveCalc {
    abstract void validateMoves();
    abstract Collection<ChessMove> getPossibleMoves();

    boolean nullPiece(ChessBoard board, ChessPosition new_position){
        return board.getPiece(new_position) == null;

    }
    boolean notNullPiece(ChessBoard board, ChessPosition currPosition, ChessPosition newPosition){
        return board.getPiece(newPosition).getTeamColor() != board.getPiece(currPosition).getTeamColor();
    }

    void addMoves(ChessPiece.PieceType type, ChessBoard board, ChessPosition currPosition,
                  ArrayList<ChessMove> moves){
        List<TypeOfMoves.Moves> possibleMoves = TypeOfMoves.getPieceWithMoves(type);
        if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.KNIGHT){
            for (TypeOfMoves.Moves move : possibleMoves) {
                ChessPosition newPosition = move.movePositions(currPosition);
                if (board.insideBoard(newPosition)){
                    if (nullPiece(board, newPosition)){
                        moves.add(new ChessMove(currPosition, newPosition, null ));
                    } else if (notNullPiece(board, currPosition, newPosition)){
                        moves.add(new ChessMove(currPosition, newPosition, null ));
                    }
                }
            }
        }

        else {
            for (TypeOfMoves.Moves move : possibleMoves) {
                ChessPosition new_position = move.movePositions(currPosition);
                while (board.insideBoard(new_position)) {
                    if (nullPiece(board, new_position)){
                        moves.add(new ChessMove(currPosition, new_position, null));
                    }
                    else if (notNullPiece(board, currPosition, new_position)){
                        moves.add(new ChessMove(currPosition, new_position, null));
                        break;
                    }
                    else{
                        break;
                    }
                    new_position = move.movePositions(new_position);
                }
            }
        }
    }

}