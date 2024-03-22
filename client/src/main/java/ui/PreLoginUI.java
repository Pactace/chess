package ui;

import ServerFacade.ServerFacade;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final NavigatorUI navigator;
    private final ServerFacade serverFacade;
    PreLoginUI(NavigatorUI navigator, ServerFacade serverFacade){
        this.navigator = navigator;
        this.serverFacade = serverFacade;
    }
    public void main(String[] args) {
        System.out.print("\u001b[36;1m");
        System.out.println("♕ Welcome to the 240 Chess Client: Type 'help' to get started");
        commandPrompt(args);
    }

    private void commandPrompt(String[] args){
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

            //look to see for a successful login or register if so break and then call login
            //consolidate command prompt.
            //check the command check of the current UI in the navigator class, then you don't have to break the loop
            boolean success = commandCheck(command);
            if(success){
                break;
            }
        }
        navigator.transferToPostLoginUI(args);
    }

    /**
     * This helper function checks what command the user has entered and
     * what function it comes from
     * We will then navigate to that page
     */
    private boolean commandCheck(String command){
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
                UserData newUser = new UserData(loginData[0], loginData[1], null);
                try{
                    serverFacade.login(newUser);
                    return true;
                }
                catch(Exception e){
                    System.out.println("Theres a problem with your login you goon either you dont exist or you dont remember your password");
                }
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
                UserData newUser = new UserData(loginData[0], loginData[1], loginData[2]);
                try{
                    serverFacade.register(newUser);
                    return true;
                }
                catch(Exception e){
                    System.out.println("Theres a problem with your register you goon pick a unique username");
                }
            }
            else {
                System.out.print("\u001b[31;1m");
                System.out.println("Theres a problem with your register you goon enter the right amount of fields(3)");
            }
        }
        else if(command.equalsIgnoreCase("clear for real tho")){
            try{
                serverFacade.clear();
            }
            catch(Exception e){
                System.out.println("clear failed");
            }

        }
        //just in case the user inputs a bad function
        else{
            System.out.print("\u001b[31;1m");
            System.out.println("Theres a problem with your command, please make sure there are no extra letters or spaces");
            System.out.println("Type 'help' if you need to see the commands again, you goon");
        }
        return false;
    }
}