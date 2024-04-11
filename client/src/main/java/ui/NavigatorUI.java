package ui;

import clientTests.ServerFacade;

public class NavigatorUI {
    //here we are going to put the serverFacade we will be using just so all the authTokens are consistent
    ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    public void transferToPreLoginUI(String[] args){
        PreLoginUI preLoginUI = new PreLoginUI(this, serverFacade);
        preLoginUI.main(args);
    }
    public void transferToPostLoginUI(String[] args){
        PostLoginUI postLoginUI = new PostLoginUI(this, serverFacade);
        postLoginUI.main(args);
    }
    public void transferToGamePlayUI(String[] args){
        GameplayUI GameplayUI = new GameplayUI(this, serverFacade);
        GameplayUI.main(args);
    }
}
