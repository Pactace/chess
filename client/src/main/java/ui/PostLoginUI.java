package ui;

import clientTests.ServerFacade;
import model.GameData;

import java.util.Scanner;
//they should all be able to implement a common abstract class. They need to able to ask a question.
public class PostLoginUI {

    private final NavigatorUI navigator;
    private final ServerFacade serverFacade;
    private int path = 0;
    private String username;
    private String authToken;
    private String color = "";

    PostLoginUI(NavigatorUI navigator, ServerFacade serverFacade, String username, String authToken){
        this.navigator = navigator;
        this.serverFacade = serverFacade;
        this.username = username;
        this.authToken = authToken;
    }
    public void main(String[] args) throws Exception {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Hey your in! Welcome to the VIP suite make yourself at home B): Type 'help' to get started");
        commandPrompt(args);
    }

    private void commandPrompt(String[] args) throws Exception {
        //this starts a loop that will continually check for inputs
        while (true) {
            System.out.print("\u001b[49;m");
            System.out.print("\u001b[32;1m");
            System.out.printf("[LOGGED IN]>>>");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            path = commandCheck(command);
            if(path != 0){
                break;
            }
        }
        if(path == -1){
            navigator.transferToPreLoginUI(args);
        }
        else{
            navigator.transferToGamePlayUI(args, path, username, authToken, color);
        }
    }

    /**
     * This helper function checks what command the user has entered and
     * what function it comes from
     * We will then navigate to that page
     */
    private int commandCheck(String command){
        if(command.equalsIgnoreCase("help")){
            //here we print the header
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("Commands that you can use on this page:");

            //then we go to the body
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[34;1m");
            System.out.println("'help' - it refreshes you on the commands");
            System.out.println("'list' - check out all the games on the server");
            System.out.println("'create' - here you can create a new game, remember you still have to use the join game if you want to play");
            System.out.println("'join' - get playing! specify the game you want to play with the game id and the color and we'll join you right up");
            System.out.println("'observe' - just watching? use this function by entering the game id");
            System.out.print("\u001b[31;1m");
            System.out.println("'logout' - this will end your session with us and send you back to the login page");
        }
        else if(command.equalsIgnoreCase("list")){
            //Here we are going to enter the username and password separated by spaces.
            try {
                System.out.print("\u001b[45;1m");
                System.out.print("\u001b[30;1m");
                System.out.println("List of Games:");
                GameData[] games = serverFacade.listGames();
                for(GameData game: games){
                    System.out.println("Game ID: "+ game.gameID() + " Game Name: " + game.gameName() +
                            " White Player: " + game.whiteUsername() + " Black Player: " + game.blackUsername());
                }
            }
            catch(Exception e){
                System.out.print(e.getMessage());
            }
        }
        else if(command.equalsIgnoreCase("create")){
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To create a new game enter your data like this (without single quotes all one word):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Game Name'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[ENTER NEW GAME NAME]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var gameParams = line.split(" ");
            if(gameParams.length != 1){
                System.out.println("what part about only 1 word do you not understand?");
            }
            else{
                try{
                    serverFacade.createGame(line);
                }
                catch(Exception e){
                    System.out.println("Sorry we cant make that for you right now for some reason, probably your own goonish behavior");
                }
            }

        }
        else if(command.equalsIgnoreCase("join")){
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To join to a already existing game enter your data like this (without single quotes, spaces between each field):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Game ID' 'Color You Wish to Play'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[JOIN INFO]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var loginData = line.split(" ");

            //if the login data is good move over to the next issue.
            if(loginData.length == 2){
                try{
                    serverFacade.joinOrObserveGame(Integer.parseInt(loginData[0]), loginData[1]);
                    color = loginData[1];
                    return Integer.parseInt(loginData[0]);
                }
                catch(Exception e){
                    System.out.println("Join didnt work");
                }

            }
            else {
                System.out.print("\u001b[31;1m");
                System.out.println("Theres a problem with your join you goon");
            }
        }
        else if(command.equalsIgnoreCase("observe")){
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To observe to a already existing game enter your data like this (without single quotes, spaces between each field):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Game ID'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[JOIN INFO]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            try{
                serverFacade.joinOrObserveGame(Integer.parseInt(line), null);

                //the gameID will be used in the transition so that we can figure out what side the person is on
                return Integer.parseInt(line);
            }
            catch(Exception e){
                System.out.println("Join didnt work");
            }
        }
        else if(command.equalsIgnoreCase("logout")){
            try {
                serverFacade.logout();
            }catch (Exception e){
                System.out.print("I literally don't know how you got this far without an authToken");
            }
            return -1;
        }
        //just in case the user inputs a bad function
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Theres a problem with your command, please make sure there are no extra letters or spaces");
            System.out.println("Type 'help' if you need to see the commands again, you goon");
        }
        return 0;
    }
}
