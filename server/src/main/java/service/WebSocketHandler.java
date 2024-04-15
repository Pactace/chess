package service;

import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Session;

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
        System.out.printf("Received: %s", message);
        //session.getRemote().sendString("WebSocket response: " + message);
    }
}
