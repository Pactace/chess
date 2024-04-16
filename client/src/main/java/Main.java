import chess.*;
import clientTests.ServerFacade;
import ui.NavigatorUI;

public class Main {
    //ask the user a question and then we just react to it. Main program responsibility
    //Each menu should be responsible for setting its own set of commands.
    //navigator class goes from place to place in the UIs
    public static void main(String[] args) throws Exception{
        NavigatorUI navigate = new NavigatorUI();
        navigate.transferToPreLoginUI(args);
    }
}