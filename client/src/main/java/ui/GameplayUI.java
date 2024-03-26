package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static java.lang.Math.abs;
import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static boolean tileColor = true;


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        out.print("    ");

        drawHeaders(out, true);

        drawChessBoard(out, true);

        out.print("    ");
        drawHeaders(out, true);

        out.print("    ");

        drawHeaders(out, false);

        drawChessBoard(out, false);

        out.print("    ");
        drawHeaders(out, false);


        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, boolean white) {

        setBlack(out);
        out.print(" ");
        String[] blackHeaders = { "a","b","c","d","e","f","g","h" };
        String[] whiteHeaders = { "h","g","f","e","d","c","b","a" };
        String[] headers;

        if(white)
            headers = whiteHeaders;
        else
            headers = blackHeaders;

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print("  ");
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, boolean white) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            colorSwitch(out);
            drawRowNumbers(out, boardRow + 1, white);
            out.print(EMPTY);
            drawRowOfSquares(out, boardRow + 1, white);
            //drawRowNumbers(out, boardRow);
        }
    }

    private static void drawRowNumbers(PrintStream out, int boardRow, boolean white){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        if(white)
            out.print(boardRow);
        else
            out.print(abs(boardRow - 9));
    }
    private static void drawRowOfSquares(PrintStream out, int rowNum, boolean white) {
        String[] blackBackLinePieces = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};
        String[] whiteBackLinePieces = {" R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R "};
        for (int squareHieght = 0; squareHieght < SQUARE_SIZE_IN_CHARS; ++squareHieght) {
            //this is the spacing
            if(squareHieght > 0){
                out.print(" ");
                out.print(EMPTY);
            }
            for (int squareWidth = 0; squareWidth < BOARD_SIZE_IN_SQUARES; ++squareWidth) {
                colorSwitch(out);
                //if we are on the top row
                if(rowNum == 1){
                    if(white){
                        setGrey(out);
                        out.print(whiteBackLinePieces[squareWidth]);
                    }
                    else{
                        setMagenta(out);
                        out.print(blackBackLinePieces[squareWidth]);
                    }
                }
                //if we are the second to top row
                else if(rowNum == 2){
                    if(white)
                        setGrey(out);

                    else
                        setMagenta(out);
                    out.print(" P ");
                }
                //if we are on the second to bottom row
                else if(rowNum == 7){
                    if(white)
                        setMagenta(out);

                    else
                        setGrey(out);
                    out.print(" P ");
                }
                //if we are the bottom row
                else if(rowNum == 8){
                    if(white){
                        setMagenta(out);
                        out.print(whiteBackLinePieces[squareWidth]);
                    }
                    else{
                        setGrey(out);
                        out.print(blackBackLinePieces[squareWidth]);
                    }
                }
                else{
                    out.print(EMPTY);
                }

                setBlack(out);
            }
            if(squareHieght == 0){
                out.print(EMPTY);
                drawRowNumbers(out, rowNum, white);
            }
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGrey(PrintStream out) {
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private static void setMagenta(PrintStream out) {
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private static void colorSwitch(PrintStream out){
        if(!tileColor){
            setWhite(out);
            tileColor = true;
        }
        else{
            setBlue(out);
            tileColor = false;
        }

    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}