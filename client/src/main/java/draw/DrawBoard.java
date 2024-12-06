package draw;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;


import java.util.Collection;

import static ui.EscapeSequences.*;


public class DrawBoard {
    private String board;
    private ChessGame.TeamColor color;
    private Collection<ChessMove> moves;
    private boolean possibleMoves;

    public DrawBoard(ChessGame.TeamColor color, String board, Boolean possibleMoves, Collection<ChessMove> moves){
        this.board = board;
        this.color = color;
        this.moves = moves;
        this.possibleMoves = possibleMoves;
    }
    public String getDrawnBoard(){
        if (color.equals(ChessGame.TeamColor.WHITE)){
            return createBoard("BLACK", "a  b  c  d  e  f  g  h",  9, "WHITE");
        }
        return createBoard("WHITE", "h  g  f  e  d  c  b  a",  0, "BLACK");
    }


    private String createBoard(String topColor, String topLetter, int startingNumber, String bottomColor) {
        StringBuilder draw = new StringBuilder();

        // Header rows
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("            %s             ", topColor))
                .append(RESET_BG_COLOR).append("\n");
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE)
                .append(String.format("    %s    ", topLetter)).append(RESET_BG_COLOR).append("\n");

        String bgColor = SET_BG_COLOR_LIGHT_GREY;
        boolean isNewRow = true;
        int rowCounter = startingNumber;
        ChessPosition position;
        position = topColor.equals("BLACK") ? new ChessPosition(8,1) : new ChessPosition(1,9);

        for (int i = topColor.equals("BLACK") ? 0 : board.length() - 1;
             topColor.equals("BLACK") ? i < board.length() : i > -1;
             i += topColor.equals("BLACK") ? 1 : -1) {
            char currentChar = board.charAt(i);
            boolean highlightMove = false;
            boolean hightlightCurrPosition = false;
            if (possibleMoves){
                if (currentPositionChecker(position)){
                    hightlightCurrPosition = true;
                }
                if (moveChecker(position)){
                    highlightMove = true;
                }
            }

            if (isNewRow) {
                rowCounter = topColor.equals("BLACK") ? rowCounter - 1 : rowCounter + 1;
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter).append(" ");
                isNewRow = false;
                if (i == 71){
                    continue;
                }
            }
            if (currentChar == ' '){
                String oldColor = bgColor;
                if (highlightMove){
                    bgColor = toggleHighlightColor(bgColor);
                } else if (hightlightCurrPosition) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }
                draw.append(bgColor).append("   ");
                bgColor = toggleColor(oldColor);
            }
            else if (currentChar == '\n'){
                isNewRow = true;
                position = topColor.equals("BLACK") ? new ChessPosition(position.getRow() - 1, 0)
                        : new ChessPosition(position.getRow() + 1, 9);
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter)
                        .append(" ").append(RESET_BG_COLOR).append("\n");
                bgColor = toggleColor(bgColor);
            }
            else {
                String oldColor = bgColor;
                if (highlightMove){
                    bgColor = toggleHighlightColor(bgColor);
                } else if (hightlightCurrPosition) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }
                draw.append(formatPiece(currentChar, bgColor));
                bgColor = toggleColor(oldColor);
            }
            if (topColor.equals("WHITE") && i == 0){
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter)
                        .append(" ").append(RESET_BG_COLOR).append("\n");
                bgColor = toggleColor(bgColor);
            }
            position = topColor.equals("BLACK") ? new ChessPosition(position.getRow(), position.getColumn() + 1)
                    : new ChessPosition(position.getRow(), position.getColumn() - 1);
        }

        // Footer rows
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("    %s    ", topLetter))
                .append(RESET_BG_COLOR).append("\n");
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE)
                .append(String.format("            %s             ", bottomColor)).append(RESET_BG_COLOR).append("\n");

        return draw.toString();
    }

    private String toggleColor(String currentColor) {
        return currentColor.equals(SET_BG_COLOR_DARK_GREY) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
    }
    private String toggleHighlightColor(String currentColor) {
        return currentColor.equals(SET_BG_COLOR_DARK_GREY) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
    }


    private String formatPiece(char piece, String bgColor) {
        if (Character.isUpperCase(piece)) {
            // White piece
            return SET_TEXT_BOLD + SET_TEXT_COLOR_RED + bgColor + " " + piece + " ";
        } else {
            // Black piece
            return SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + bgColor + " " + Character.toUpperCase(piece) + " ";
        }
    }
    private boolean moveChecker(ChessPosition position){
        for (ChessMove move : moves){
            if (move.getEndPosition().equals(position)){
                return true;
            }
        }
        return false;
    }
    private boolean currentPositionChecker(ChessPosition position){
        for (ChessMove move : moves){
            if (move.getStartPosition().equals(position)){
                return true;
            }
        }
        return false;
    }
}
