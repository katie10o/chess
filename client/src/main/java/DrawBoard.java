import static ui.EscapeSequences.*;


public class DrawBoard {
    private String board;
    private String whiteBoard;
    private String blackBoard;

    public DrawBoard(String board){
        this.board = board;
        whiteBoard = createBoard("BLACK", "a  b  c  d  e  f  g  h",  9, "WHITE");
        blackBoard = createBoard("WHITE", "h  g  f  e  d  c  b  a",  0, "BLACK");
    }
    public String getDrawnBoard(){
        return whiteBoard + "\n" + blackBoard;
    }

    private String createBoard(String topColor, String topLetter, int startingNumber, String bottomColor) {
        StringBuilder draw = new StringBuilder();

        // Header rows
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("            %s             ", topColor)).append(RESET_BG_COLOR).append("\n");
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("    %s    ", topLetter)).append(RESET_BG_COLOR).append("\n");

        String bgColor = SET_BG_COLOR_LIGHT_GREY;
        boolean isNewRow = true;
        int rowCounter = startingNumber;

        for (int i = topColor.equals("BLACK") ? 0 : board.length() - 1;
             topColor.equals("BLACK") ? i < board.length() : i > -1;
             i += topColor.equals("BLACK") ? 1 : -1) {
            char currentChar = board.charAt(i);


            if (isNewRow) {
                rowCounter = topColor.equals("BLACK") ? rowCounter - 1 : rowCounter + 1;
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter).append(" ");
                isNewRow = false;
                if (i == 71){
                    continue;
                }
            }
            if (currentChar == ' '){
                draw.append(bgColor).append("   ");
                bgColor = toggleColor(bgColor);
            }
            else if (currentChar == '\n'){
                isNewRow = true;
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter).append(" ").append(RESET_BG_COLOR).append("\n");
                bgColor = toggleColor(bgColor);
            }
            else {
                draw.append(formatPiece(currentChar, bgColor));
                bgColor = toggleColor(bgColor);
            }
            if (topColor.equals("WHITE") && i == 0){
                draw.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE).append(" ").append(rowCounter).append(" ").append(RESET_BG_COLOR).append("\n");
                bgColor = toggleColor(bgColor);
            }
        }

        // Footer rows
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("    %s    ", topLetter)).append(RESET_BG_COLOR).append("\n");
        draw.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE).append(String.format("            %s             ", bottomColor)).append(RESET_BG_COLOR).append("\n");

        return draw.toString();
    }

    private String toggleColor(String currentColor) {
        return currentColor.equals(SET_BG_COLOR_DARK_GREY) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
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
}
