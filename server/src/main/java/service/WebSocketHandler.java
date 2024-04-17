package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private Session session;
    private SessionsManager sessionsManager;

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        sessionsManager = new SessionsManager();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(message, session);
            case JOIN_OBSERVER -> joinObserver(message, session);
            case LEAVE -> leave(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case RESIGN -> resign(message, session);
        }
    }

    //this is the first bit we have to figure out tomorrow
    private void joinPlayer(String message, Session session) throws Exception {
        JoinPlayer joinPlayerRequest = new Gson().fromJson(message, JoinPlayer.class);
        AuthData authData = authDAO.getAuthData(joinPlayerRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(joinPlayerRequest.getGameID()) != null)){
            //if the move is made by the person who joined
            if((joinPlayerRequest.getPlayerColor() == ChessGame.TeamColor.WHITE && Objects.equals(gameDAO.getGame(joinPlayerRequest.getGameID()).whiteUsername(), authData.username())) ||
            (joinPlayerRequest.getPlayerColor() == ChessGame.TeamColor.BLACK && Objects.equals(gameDAO.getGame(joinPlayerRequest.getGameID()).blackUsername(), authData.username()))){
                sessionsManager.addSessionToGame(joinPlayerRequest.getGameID(), joinPlayerRequest.getAuthString(), session);

                //here we are going to do the stuff for the root user
                GameData gameData = gameDAO.getGame(joinPlayerRequest.getGameID());
                var loadGame = new LoadGame(gameData.game());
                session.getRemote().sendString(new Gson().toJson(loadGame));

                //here we are broadcasting it to everyone else
                var notificationMessage = String.format("%s is in the game as a player", authData.username());
                var notification = new Notification(notificationMessage);
                var messageToOtherClients = new Gson().toJson(notification);
                sessionsManager.sendToOtherClients(joinPlayerRequest.getGameID(), joinPlayerRequest.getAuthString(), messageToOtherClients);
            }
        }
    }
    private void joinObserver(String message, Session session) throws Exception {
        JoinObserver joinObserverRequest = new Gson().fromJson(message, JoinObserver.class);
        AuthData authData = authDAO.getAuthData(joinObserverRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(joinObserverRequest.getGameID()) != null)){
            sessionsManager.addSessionToGame(joinObserverRequest.getGameID(), joinObserverRequest.getAuthString(), session);
            //here we are going to do the stuff for the root user
            GameData gameData = gameDAO.getGame(joinObserverRequest.getGameID());
            var loadGame = new LoadGame(gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadGame));

            //here we are broadcasting it to everyone else
            var notificationMessage = String.format("%s is in the game as an observer", authData.username());
            var notification = new Notification(notificationMessage);
            var messageToOtherClients = new Gson().toJson(notification);
            sessionsManager.sendToOtherClients(joinObserverRequest.getGameID(), joinObserverRequest.getAuthString(), messageToOtherClients);
        }
    }
    private void leave(String message, Session session) throws Exception {
        Leave leaveRequest = new Gson().fromJson(message, Leave.class);
        AuthData authData = authDAO.getAuthData(leaveRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(leaveRequest.getGameID()) != null)){
            sessionsManager.removeSessionFromGame(leaveRequest.getGameID(), leaveRequest.getAuthString());

            //here we are broadcasting it to everyone else
            var notificationMessage = String.format("%s left the game", authData.username());
            var notification = new Notification(notificationMessage);
            var messageToOtherClients = new Gson().toJson(notification);
            sessionsManager.sendToOtherClients(leaveRequest.getGameID(), leaveRequest.getAuthString(), messageToOtherClients);
        }
    }
    private void makeMove(String message, Session session) throws Exception {
        MakeMove makeMoveRequest = new Gson().fromJson(message, MakeMove.class);
        AuthData authData = authDAO.getAuthData(makeMoveRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(makeMoveRequest.getGameID()) != null)){
            //get ze game
            GameData gameData = gameDAO.getGame(makeMoveRequest.getGameID());
            //make ze move
            gameData.game().makeMove(makeMoveRequest.getMove());
            //upzate ze game
            gameDAO.updateGame(gameData);

            //here we are going to do the stuff for the root user
            gameData = gameDAO.getGame(makeMoveRequest.getGameID());
            var loadGame = new LoadGame(gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadGame));
            var messageToOtherClients = new Gson().toJson(loadGame);
            sessionsManager.sendToOtherClients(makeMoveRequest.getGameID(), makeMoveRequest.getAuthString(), messageToOtherClients);

            //here we are broadcasting it to everyone else
            var notificationMessage = String.format("%s %s was played", makeMoveRequest.getMove().getStartPosition(), makeMoveRequest.getMove().getStartPosition());
            var notification = new Notification(notificationMessage);
            messageToOtherClients = new Gson().toJson(notification);
            sessionsManager.sendToOtherClients(makeMoveRequest.getGameID(), makeMoveRequest.getAuthString(), messageToOtherClients);
        }
    }
    private void resign(String message, Session session) throws Exception {
        Resign resignRequest = new Gson().fromJson(message, Resign.class);
        AuthData authData = authDAO.getAuthData(resignRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(resignRequest.getGameID()) != null)){

            //here we are broadcasting it to everyone else
            var notificationMessage = String.format("%s has resigned: the game has ended", authData.username());
            var notification = new Notification(notificationMessage);
            session.getRemote().sendString(new Gson().toJson(notification));
            var messageToOtherClients = new Gson().toJson(notification);
            sessionsManager.sendToOtherClients(resignRequest.getGameID(), resignRequest.getAuthString(), messageToOtherClients);
        }
    }

}
