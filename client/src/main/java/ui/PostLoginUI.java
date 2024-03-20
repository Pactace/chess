package ui;

import java.util.Scanner;

//they should all be able to implement a common abstract class. They need to able to ask a question.
public class PostLoginUI {
    public static void main(String[] args) {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Hey your in! Welcome to the VIP suite make yourself at home B): Type 'help' to get started");
        commandPrompt();
    }

    public static void commandPrompt(){
        //this starts a loop that will continually check for inputs
        while (true) {
            System.out.print("\u001b[49;m");
            System.out.print("\u001b[32;1m");
            System.out.printf("[LOGGED IN]>>>");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            if(command.equalsIgnoreCase("logout")){
                break;
            }
            commandCheck(command, "main");
        }
    }

    /**
     * This helper function checks what command the user has entered and
     * what function it comes from
     * We will then navigate to that page
     */
    public static void commandCheck(String command, String function){
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
            System.out.print("\u001b[45;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("List of Games:");
        }
        else if(command.equalsIgnoreCase("create")){
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To create a new game enter your data like this (without single quotes):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Game Name'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[ENTER NEW GAME NAME]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
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
        }
        //just in case the user inputs a bad function
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Theres a problem with your command, please make sure there are no extra letters or spaces");
            System.out.println("Type 'help' if you need to see the commands again, you goon");
        }
    }
}
