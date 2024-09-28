package chess;

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
        List<TypeOfMoves.moves> possibleMoves = TypeOfMoves.getPieceWithMoves(type);
        if (type == ChessPiece.PieceType.KING || type == ChessPiece.PieceType.KNIGHT){
            for (TypeOfMoves.moves move : possibleMoves) {
                ChessPosition new_position = move.movePositions(currPosition);
                if (board.extendsBoard(new_position)){
                    if (nullPiece(board, new_position) || notNullPiece(board, currPosition, new_position )){
                        moves.add(new ChessMove(currPosition, new_position, null ));
                    }
                }
            }
        }

        else {
            for (TypeOfMoves.moves move : possibleMoves) {
                ChessPosition new_position = move.movePositions(currPosition);
                while (board.extendsBoard(new_position)) {
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
