package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 2;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static boolean tileColor = true;


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        out.print("    ");

        drawHeaders(out);

        drawChessBoard(out);

        out.print("    ");
        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { "a","b","c","d","e","f","g","h" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
        // int suffixLength = SQUARE_SIZE_IN_CHARS / 2;
        out.print("  ");
        printHeaderText(out, headerText);
        //out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            colorSwitch(out);
            drawRowNumbers(out, boardRow + 1);
            out.print(EMPTY);
            drawRowOfSquares(out, boardRow + 1);
            //drawRowNumbers(out, boardRow);
        }
    }

    private static void drawRowNumbers(PrintStream out, int boardRow){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(boardRow);
    }
    private static void drawRowOfSquares(PrintStream out, int rowNum) {
        for (int squareHieght = 0; squareHieght < SQUARE_SIZE_IN_CHARS; ++squareHieght) {
            if(squareHieght > 0){
                out.print(" ");
                out.print(EMPTY);
            }
            for (int squareWidth = 0; squareWidth < BOARD_SIZE_IN_SQUARES; ++squareWidth) {
                colorSwitch(out);

                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));

                setBlack(out);
            }
            if(squareHieght == 0){
                out.print(EMPTY);
                drawRowNumbers(out, rowNum);
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