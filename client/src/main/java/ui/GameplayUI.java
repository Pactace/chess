package ui;

import chess.*;
import clientTests.ServerFacade;
import model.GameData;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import websocket.GameHandler;
import websocket.ResponseException;
import websocket.WebSocketFacade;

import java.util.Objects;
import java.util.Scanner;

import static ui.BoardCreation.*;

public class GameplayUI implements GameHandler {
    private final NavigatorUI navigator;
    private final WebSocketFacade webSocketFacade;
    private final int gameID;
    private final String username;
    private final String authToken;
    private static ChessGame.TeamColor teamColor = null;
    private static String color = "";
    public static ChessBoard board;
    public static ChessGame game;


    GameplayUI(NavigatorUI navigator, int gameID, String username, String authToken, String color) throws Exception {
        webSocketFacade = new WebSocketFacade(this);
        this.navigator = navigator;
        this.gameID = gameID;
        this.username = username;
        this.authToken = authToken;
        this.color = color;
        if(color.equalsIgnoreCase("WHITE"))
            teamColor = ChessGame.TeamColor.WHITE;
        else if(color.equalsIgnoreCase("BLACK"))
            teamColor = ChessGame.TeamColor.BLACK;
    }
    public void main(String[] args) throws Exception {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the game chosen one your trial starts now: Type 'help' to get started");
        if(teamColor != null){
            try {
                webSocketFacade.joinPlayer(authToken,gameID,teamColor);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try {
                webSocketFacade.joinObserver(authToken,gameID);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
        commandPrompt(args);
    }

    private void commandPrompt(String[] args) throws Exception {
        //this starts a loop that will continually check for inputs
        while (true) {
            System.out.print("\u001b[49;m");
            System.out.print("\u001b[32;1m");
            System.out.println("[IN GAME]>>>");
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
        else if(command.equalsIgnoreCase("move")) {
            if (game.getTeamTurn() == teamColor) {
                move();
            }
            else{
                System.out.print("\u001b[31;1m");
                System.out.println("You can only move on your turn >:C");
            }
        }
        else if(command.equalsIgnoreCase("resign")){
            resign();
        }
        else if(command.equalsIgnoreCase("leave")) {
            //here all we need to do is take the user out
            try {
                webSocketFacade.leave(authToken, gameID);
                navigator.transferToPostLoginUI(args, username, authToken);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void resign() {
        System.out.print("\u001b[104;1m");
        System.out.print("\u001b[30;1m");
        System.out.println("To confirm the resign either type 'Y' or 'N':");

        System.out.print("\u001b[49;m");
        System.out.print("\u001b[33;1m");
        System.out.printf("[CONFIRM YOUR CHOICE]>>>");

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if(Objects.equals(line, "Y")) {
            try {
                webSocketFacade.resign(authToken, gameID);
            } catch (ResponseException e) {
                System.out.print("\u001b[31;1m");
                System.out.println("we got a problem");
            }
        }
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Canceled resign request");
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

    private void legal(){
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

    private void move() {
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
                try{
                    webSocketFacade.makeMove(authToken, gameID, move);
                }
                catch (ResponseException e) {
                    System.out.print("\u001b[31;1m"); System.out.println("we got a problem");
                }
            } catch (InvalidMoveException e) {
                System.out.print("\u001b[31;1m");
                System.out.println("wait, that's illegal");
            } catch (NullPointerException e) {
                System.out.print("\u001b[31;1m");
                System.out.println("um are you sure you inputted the right piece?");
            }
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


    @Override
    public void notify(Notification serverMessage) {
        System.out.print("\u001b[107;1m");
        System.out.print("\u001b[34;1m");
        System.out.println(serverMessage.getMessage());
    }

    @Override
    public void error(Error serverMessage) {
        System.out.print("\u001b[31;1m");
        System.out.println("ERROR: " + serverMessage.getMessage());
    }

    @Override
    public void loadGame(LoadGame serverMessage) {
        game = serverMessage.getGame();
        board = game.getBoard();
        setBoard(board);
        boardCreation(color);
    }
}