package ui;

import java.util.Scanner;

public class PreLoginUI {
    public static void main(String[] args) {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the 240 Chess Client: Type 'help' to get started");
        //this starts a loop that will continually check for inputs
        while (true) {
            System.out.print("\u001b[32;1m");
            System.out.printf("[LOGGED OUT]>>>");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            //if the command is quit just end the program.
            if(command.equalsIgnoreCase("quit")){
                System.out.println("Exiting page: come back soon :D");
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
            System.out.println("'register' - put in a name, password and email and join the club :D");
            System.out.println("'login' - if your are already a member, put in your username and password");
            System.out.print("\u001b[31;1m");
            System.out.println("'quit' - this will end the program");


            //then we reset everything
            System.out.print("\u001b[49;m");
        }
        else if(command.equalsIgnoreCase("login")){
            while(true){
                //Here we are going to enter the username and password separated by spaces.

            }
        }
        else if(command.equalsIgnoreCase("register")){
            while(true){
                //here we are going to ask for a username password and email seperated by spaces

            }
        }
        //just in case the user inputs a bad function
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Theres a problem with your command, please make sure there are no extra letters or spaces");
            System.out.println("Type 'help' if you need to see the commands again, you goon");
        }
    }
}