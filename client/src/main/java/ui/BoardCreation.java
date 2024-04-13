package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.lang.Math.abs;
import static ui.EscapeSequences.*;

public class BoardCreation {
    private static ChessBoard board;
    private static boolean tileColor = true;
    private static final String EMPTY = "   ";
    public static boolean[][] legalMoves = new boolean[8][8];

    public static void setBoard(ChessBoard passedBoard){
        board = passedBoard;
    }
    public static void boardCreation(String color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        //here we are going to create a board depending on the player side
        out.print(ERASE_SCREEN);

        if(color.equalsIgnoreCase("WHITE") || color == ""){
            out.print("    ");

            drawHeaders(out, true);

            drawChessBoard(out, true);

            out.print("    ");
            drawHeaders(out, true);
        }

        //here we are going to do an if statement again because if the observer is looking we want to print both
        if(color.equalsIgnoreCase("BLACK") || color == "") {
            out.print("    ");

            drawHeaders(out, false);

            drawChessBoard(out, false);

            out.print("    ");
            drawHeaders(out, false);
        }


        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, boolean white) {

        setBlack(out);
        out.print(" ");
        String[] whiteHeaders = { "a","b","c","d","e","f","g","h" };
        String[] blackHeaders = { "h","g","f","e","d","c","b","a" };
        String[] headers;

        if(white)
            headers = whiteHeaders;
        else
            headers = blackHeaders;

        for (int boardCol = 0; boardCol < 8; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < 8 - 1) {
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
        int startRow = (!white ? 0 : 8 - 1);
        int endRow = (!white ? 8 : -1);
        int rowIncrement = (!white ? 1 : -1);

        for (int boardRow = startRow; boardRow != endRow; boardRow += rowIncrement) {

            colorSwitch(out, legalMoves[boardRow][0]);
            drawRowNumbers(out, boardRow + 1, white);
            out.print(EMPTY);
            drawRowOfSquares(out, boardRow + 1, white);
        }
    }

    private static void drawRowNumbers(PrintStream out, int boardRow, boolean white){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        if(!white)
            out.print(boardRow);
        else
            out.print(abs(boardRow - 9));
    }
    private static void drawRowOfSquares(PrintStream out, int rowNum, boolean white) {
        int startSquare = (!white ? 8 : 1);
        int endSquare = (!white ? 0 : 9);
        int increment = (!white ? -1 : 1);

        for (int colNum = startSquare; colNum != endSquare; colNum += increment) {
            //if legal moves have a true at this position then make sure that color switch prints out the greens
            colorSwitch(out, legalMoves[rowNum-1][colNum-1]);

            if (board.getPiece(new ChessPosition(rowNum, colNum)) != null) {
                ChessPiece piece = board.getPiece(new ChessPosition(rowNum, colNum));
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setMagenta(out);
                } else {
                    setGrey(out);
                }
                switch (piece.getPieceType()) {
                    case ROOK:
                        out.print(" R ");
                        break;
                    case KNIGHT:
                        out.print(" N ");
                        break;
                    case BISHOP:
                        out.print(" B ");
                        break;
                    case QUEEN:
                        out.print(" Q ");
                        break;
                    case KING:
                        out.print(" K ");
                        break;
                    case PAWN:
                        out.print(" P ");
                        break;
                }
            } else {
                out.print(EMPTY);
            }
            setBlack(out);
        }

        out.print(EMPTY);
        drawRowNumbers(out, rowNum, white);
        out.println();
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }
    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
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

    private static void colorSwitch(PrintStream out, boolean validMove){
        if(!tileColor){
            if(!validMove)
                setWhite(out);
            else
                setGreen(out);

            tileColor = true;
        }
        else{
            if(!validMove)
                setBlue(out);
            else
                setDarkGreen(out);

            tileColor = false;
        }

    }
}
