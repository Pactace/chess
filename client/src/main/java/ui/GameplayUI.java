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
import static ui.BoardCreation.boardCreation;
import static ui.BoardCreation.setBoard;
import static ui.EscapeSequences.*;

public class GameplayUI {
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
        setBoard(board);
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
            boardCreation(color);
        }
        else if(command.equalsIgnoreCase("legal")){
            legal();
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

    private static void legal(){
        //Here we are going to enter the username and password separated by spaces.
        System.out.print("\u001b[104;1m");
        System.out.print("\u001b[30;1m");
        System.out.println("To see what moves are legal by a piece enter the piece position like this (without the dash, or quotation marks):");
        System.out.print("\u001b[107;1m");
        System.out.print("\u001b[35;1m");
        System.out.println("'letter-number'");

        System.out.print("\u001b[49;m");
        System.out.print("\u001b[33;1m");
        System.out.printf("[ENTER YOUR PIECE QUERY]>>>");

        Scanner scanner = new Scanner(System.in);
        char[] input = scanner.nextLine().toCharArray();
        if(input.length == 2){
            ChessPosition chessPosition = convertToChessPosition(input);
        }
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Make sure you input the right amount of characters dweeb");
        }
    }

    /*
    here we are going to go through each of the characters and depending on which character the player
    inputted we will convert that to its column number
    if there is no match we will tell the user to put in a valid input
    */
    private static ChessPosition convertToChessPosition(char[] input) {
        char[] characterList = {'a','b','c','d','e','f','g','h'};
        int colNum = 0;
        ChessPosition chessPosition = null;

        for(int i = 0; i < characterList.length; i++){
            if(input[0] == characterList[i]){
                colNum = i + 1;
            }
        }
        //if the input is not a character in range or a digit in range
        if((colNum == 0) || (Integer.parseInt("" + input[1]) < 1 ) || (Integer.parseInt("" + input[1]) > 8 )) {
            System.out.print("\u001b[31;1m");
            System.out.println("Make sure your input is correct you dweeb");
        }
        else{
            chessPosition = new ChessPosition(input[1], colNum);
        }
        return chessPosition;
    }


}