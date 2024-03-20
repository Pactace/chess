package ui;

import java.util.Scanner;

public class PreLoginUI {
    public static void main(String[] args) {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the 240 Chess Client: Type 'help' to get started");
        commandPrompt();
    }

    public static void commandPrompt(){
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

            commandCheck(command);
        }
    }

    /**
     * This helper function checks what command the user has entered and
     * what function it comes from
     * We will then navigate to that page
     */
    public static void commandCheck(String command){
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
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To login to a old account enter your data like this (without single quotes, spaces between each field):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Username' 'Password'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[ENTER YOUR LOGIN INFO]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var loginData = line.split(" ");

            //if the login data is good move over to the next issue.
            if(loginData.length == 2){

            }
            else {
                System.out.print("\u001b[31;1m");
                System.out.println("Theres a problem with your login you goon");
            }
        }
        else if(command.equalsIgnoreCase("register")){
            //Here we are going to enter the username and password separated by spaces.
            System.out.print("\u001b[104;1m");
            System.out.print("\u001b[30;1m");
            System.out.println("To create a new account enter your data like this (without single quotes, spaces between each field):");
            System.out.print("\u001b[107;1m");
            System.out.print("\u001b[35;1m");
            System.out.println("'Username' 'Password' 'Email'");

            System.out.print("\u001b[49;m");
            System.out.print("\u001b[33;1m");
            System.out.printf("[ENTER YOUR NEW INFO]>>>");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var loginData = line.split(" ");

            //if the login data is good move over to the next issue.
            if(loginData.length == 3){

            }
            else {
                System.out.print("\u001b[31;1m");
                System.out.println("Theres a problem with your register you goon");
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