package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
    private static ChessBoard board;



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
        if(game == null){
            game = new ChessGame();
            game.setBoard(new ChessBoard());
            board = game.getBoard();
            board.resetBoard();
        }
        boardCreation(color);
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
        String[] whiteHeaders = { "a","b","c","d","e","f","g","h" };
        String[] blackHeaders = { "h","g","f","e","d","c","b","a" };
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
        int startRow = (!white ? 0 : BOARD_SIZE_IN_SQUARES - 1);
        int endRow = (!white ? BOARD_SIZE_IN_SQUARES : -1);
        int rowIncrement = (!white ? 1 : -1);

        for (int boardRow = startRow; boardRow != endRow; boardRow += rowIncrement) {
            colorSwitch(out);
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
        int startWidth = (!white ? 8 : 1);
        int endWidth = (!white ? 0 : 9);
        int widthIncrement = (!white ? -1 : 1);

        for (int squareWidth = startWidth; squareWidth != endWidth; squareWidth += widthIncrement) {
            colorSwitch(out);
            if (board.getPiece(new ChessPosition(rowNum, squareWidth)) != null) {
                ChessPiece piece = board.getPiece(new ChessPosition(rowNum, squareWidth));
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