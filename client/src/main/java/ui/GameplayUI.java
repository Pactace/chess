package ui;

import chess.*;
import clientTests.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static ui.BoardCreation.*;

public class GameplayUI {
    private final NavigatorUI navigator;
    private final ServerFacade serverFacade;
    private final int gameID;
    private final String username;
    private final String authToken;
    private static String color;
    private static ChessBoard board;
    private static ChessGame game;


    GameplayUI(NavigatorUI navigator, ServerFacade serverFacade, int gameID, String username, String authToken, String color){
        this.navigator = navigator;
        this.serverFacade = serverFacade;
        this.gameID = gameID;
        this.username = username;
        this.authToken = authToken;
        this.color = color;
    }
    public void main(String[] args) throws Exception {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the game chosen one your trial starts now: Type 'help' to get started");
        GameData[] games = serverFacade.listGames();
        game = games[gameID - 1].game();
        if(game == null){
            game = new ChessGame();
            game.setBoard(new ChessBoard());
            board = game.getBoard();
            board.resetBoard();
            game.setTeamTurn(ChessGame.TeamColor.WHITE);
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
            move();
        }
        else if(command.equalsIgnoreCase("resign")){

        }
        else if(command.equalsIgnoreCase("leave")) {
            //here all we need to do is take the user out
            navigator.transferToPostLoginUI(args, username, authToken);
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
        //Here we are going to enter the chess position to get the legal moves
        System.out.print("\u001b[104;1m");
        System.out.print("\u001b[30;1m");
        System.out.println("To make a move format the move like this(without the dash, or quotation marks):");
        System.out.print("\u001b[107;1m");
        System.out.print("\u001b[35;1m");
        System.out.println("'letter-number'");

        System.out.print("\u001b[49;m");
        System.out.print("\u001b[33;1m");
        System.out.printf("[ENTER YOUR PIECE MOVE]>>>");

        Scanner scanner = new Scanner(System.in);
        char[] input = scanner.nextLine().toCharArray();
        if(input.length == 2){
            ChessPosition chessPosition = convertToChessPosition(input);
            //for each of the valid moves we are going to store the rows and columns
            ChessMove[] chessMoves = game.validMoves(chessPosition).toArray(new ChessMove[0]);
            board = game.getBoard();
            boolean[][] locations = new boolean[8][8];
            for (ChessMove chessMove : chessMoves) {
                //we are going to create a list of possible moves
                int row = chessMove.getEndPosition().getRow();
                int col = chessMove.getEndPosition().getColumn();

                locations[row-1][col-1] = true;
            }
            legalMoves = locations;
            setBoard(board);
            boardCreation(color);
            legalMoves = new boolean[8][8];
        }
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Make sure you input the right amount of characters dweeb");
        }
    }

    private static void move() {
        //Here we are going to enter the chess position to get the legal moves
        System.out.print("\u001b[104;1m");
        System.out.print("\u001b[30;1m");
        System.out.println("To see what moves are legal by a piece enter the piece position like this (without the dash, or quotation marks):");
        System.out.print("\u001b[107;1m");
        System.out.print("\u001b[35;1m");
        System.out.println("'letter-number' 'letter-number'");

        System.out.print("\u001b[49;m");
        System.out.print("\u001b[33;1m");
        System.out.print("[ENTER YOUR PIECE MOVE]>>>");

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var loginData = line.split(" ");

        char[] startInput = loginData[0].toCharArray();
        char[] endInput = loginData[1].toCharArray();
        if(startInput.length == 2 && endInput.length == 2){
            ChessPosition startPosition = convertToChessPosition(startInput);
            ChessPosition endPosition = convertToChessPosition(endInput);
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                System.out.print("\u001b[31;1m");
                System.out.println("wait, that's illegal");
            } catch (NullPointerException e) {
                System.out.print("\u001b[31;1m");
                System.out.println("um are you sure you inputted the right piece?");
            }
            board = game.getBoard();
            setBoard(board);
            boardCreation(color);
        }
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Make sure you input the right amount of characters dweeb");
        }
    }

    /**
    This is a helper function to convert the user input to a valid chess position
    */
    private static ChessPosition convertToChessPosition(char[] input) {
        int rowNum = Integer.parseInt("" + input[1]);
        char[] characterList = {'a','b','c','d','e','f','g','h'};
        int colNum = 0;
        ChessPosition chessPosition = null;

        for(int i = 0; i < characterList.length; i++){
            if(input[0] == characterList[i]){
                colNum = i + 1;
            }
        }
        //if the input is not a character in range or a digit in range
        if((rowNum < 1 ) || (rowNum > 8 ) || (colNum == 0)) {
            System.out.print("\u001b[31;1m");
            System.out.println("Make sure your input is correct you dweeb");
        }
        else{
            chessPosition = new ChessPosition(rowNum, colNum);
        }
        return chessPosition;
    }


}