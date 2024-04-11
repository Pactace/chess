package ui;

import chess.ChessBoard;
import chess.ChessGame;
import clientTests.ServerFacade;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.abs;
import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static boolean tileColor = true;

    private final NavigatorUI navigator;
    private final ServerFacade serverFacade;
    private final int gameID;
    private final String username;
    private static String color;



    GameplayUI(NavigatorUI navigator, ServerFacade serverFacade, int gameID, String username, String color){
        this.navigator = navigator;
        this.serverFacade = serverFacade;
        this.gameID = gameID;
        this.username = username;
        this.color = color;
    }
    public void main(String[] args) throws Exception {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the game chosen one your trial starts now: Type 'help' to get started");
        GameData[] games = serverFacade.listGames();
        ChessGame game = games[gameID - 1].game();
        ChessBoard gameBoard = game.getBoard();
        commandPrompt(args);
    }

    private void commandPrompt(String[] args) throws Exception {
        //this starts a loop that will continually check for inputs
        while (true) {
            System.out.print("\u001b[49;m");
            System.out.print("\u001b[32;1m");
            System.out.printf("[IN GAME]>>>");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            commandCheck(command, args);
        }
    }

    /**
     * This helper function checks what command the user has entered and
     * what function it comes from
     * We will then navigate to that page
     */
    private void commandCheck(String command, String[] args) throws Exception {
        if(command.equalsIgnoreCase("help")){
            help();
        }
        else if(command.equalsIgnoreCase("redraw")){

        }
        else if(command.equalsIgnoreCase("legal")){

        }
        else if(command.equalsIgnoreCase("move")){

        }
        else if(command.equalsIgnoreCase("resign")){

        }
        else if(command.equalsIgnoreCase("leave")) {
            //here all we need to do is take the user out
            navigator.transferToPostLoginUI(args, username);
        }
    }

    private static void help() {
        //here we print the header
        System.out.print("\u001b[104;1m");
        System.out.print("\u001b[30;1m");
        System.out.println("Commands that you can use on this page:");

        //then we go to the body
        System.out.print("\u001b[107;1m");
        System.out.print("\u001b[34;1m");
        System.out.println("'help' - it refreshes you on the commands");
        if(color != ""){
            System.out.println("'redraw' - this will redraw the chessBoard");
            System.out.println("'legal' - this will highlight all the legal moves for a specified piece");
            System.out.println("'move' - here you can make a move on a specified piece");
            System.out.print("\u001b[31;1m");
            System.out.println("'resign' - this is how you resign the game. after this no more moves can be made");
        }
        System.out.print("\u001b[31;1m");
        System.out.println("'leave' - this will end your session with us and send you back to the post-login page");
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