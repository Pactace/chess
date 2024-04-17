package ui;

import clientTests.ServerFacade;
import websocket.WebSocketFacade;

public class NavigatorUI {
    //here we are going to put the serverFacade we will be using just so all the authTokens are consistent

    ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    public void transferToPreLoginUI(String[] args) throws Exception {
        PreLoginUI preLoginUI = new PreLoginUI(this, serverFacade);
        preLoginUI.main(args);
    }
    public void transferToPostLoginUI(String[] args, String username, String authToken) throws Exception {
        PostLoginUI postLoginUI = new PostLoginUI(this, serverFacade, username, authToken );
        postLoginUI.main(args);
    }
    public void transferToGamePlayUI(String[] args, int gameID, String username, String authToken, String color) throws Exception {
        GameplayUI gameplayUI = new GameplayUI(this, gameID, username, authToken, color);
        gameplayUI.main(args);
    }
}
