package ui;

import clientTests.ServerFacade;

public class NavigatorUI {
    //here we are going to put the serverFacade we will be using just so all the authTokens are consistent

    ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    public void transferToPreLoginUI(String[] args){
        PreLoginUI preLoginUI = new PreLoginUI(this, serverFacade);
        preLoginUI.main(args);
    }
    public void transferToPostLoginUI(String[] args, String username){
        PostLoginUI postLoginUI = new PostLoginUI(this, serverFacade, username);
        postLoginUI.main(args);
    }
    public void transferToGamePlayUI(String[] args, int gameID, String username, String color){
        GameplayUI GameplayUI = new GameplayUI(this, serverFacade, gameID, username, color);
        GameplayUI.main(args);
    }
}
