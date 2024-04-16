package service;

import com.google.gson.Gson;
import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.userCommands.UserGameCommand;

public class WebSocketHandler {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private Session session;

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session);
            case JOIN_OBSERVER -> observePlayer(userGameCommand.getAuthString(), session);
            case LEAVE -> leave(userGameCommand.getAuthString(), session);
            case MAKE_MOVE -> makeMove(userGameCommand.getAuthString(), session);
            case RESIGN -> resign(userGameCommand.getAuthString(), session);
        }
    }

    //this is the first bit we have to figure out tomorrow
    private void joinPlayer(String authToken, Session session) throws Exception {}
    private void observePlayer(String authToken, Session session) throws Exception {}
    private void leave(String authToken, Session session) throws Exception {}
    private void makeMove(String authToken, Session session) throws Exception {}
    private void resign(String authToken, Session session) throws Exception {}

}
