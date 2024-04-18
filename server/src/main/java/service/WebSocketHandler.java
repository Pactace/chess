package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
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
            case LEAVE -> leave(message);
            case MAKE_MOVE -> makeMove(message, session);
            case RESIGN -> resign(message, session);
        }
    }

    //this is the first bit we have to figure out tomorrow
    private void joinPlayer(String message, Session session) throws Exception {
        JoinPlayer joinPlayerRequest = new Gson().fromJson(message, JoinPlayer.class);
        AuthData authData = authDAO.getAuthData(joinPlayerRequest.getAuthString());

        try{
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
                else{
                    var error = new Error("lowkey don't know how you got here");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
            }
            else{
                var error = new Error("either the game doesnt exist or you dont");
                session.getRemote().sendString(new Gson().toJson(error));
            }
        }catch (Exception e){
            var error = new Error("something is funky with your auth or gameId");
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }
    private void joinObserver(String message, Session session) throws Exception {
        JoinObserver joinObserverRequest = new Gson().fromJson(message, JoinObserver.class);
        AuthData authData = authDAO.getAuthData(joinObserverRequest.getAuthString());

        try {
            //make sure the gameID and the authToken and correct
            if ((authData.username() != null) && (gameDAO.getGame(joinObserverRequest.getGameID()) != null)) {
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
            } else {
                var error = new Error("something is with your auth or gameId");
                session.getRemote().sendString(new Gson().toJson(error));
            }
        } catch (Exception e){
            var error = new Error("something is funky with your auth or gameId");
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }
    private void leave(String message) throws Exception {
        Leave leaveRequest = new Gson().fromJson(message, Leave.class);
        AuthData authData = authDAO.getAuthData(leaveRequest.getAuthString());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(leaveRequest.getGameID()) != null)){
            sessionsManager.removeSessionFromGame(leaveRequest.getGameID(), leaveRequest.getAuthString());
            //here we are going to update the database
            //get ze game
            GameData gameData = gameDAO.getGame(leaveRequest.getGameID());
            //kill ze player based on color because we are razist
            GameData newGameData = gameData;
            if(Objects.equals(authData.username(), gameData.whiteUsername())){
                 newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
            else if(Objects.equals(authData.username(), gameData.blackUsername())){
                newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            }
            //upzate ze game
            gameDAO.updateGame(newGameData);

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
        GameData gameData = gameDAO.getGame(makeMoveRequest.getGameID());

        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(makeMoveRequest.getGameID()) != null)){
            //here we make sure only the person who's turn it is playing and that they are touching the right piece
            ChessGame game = gameData.game();
            if(!game.isGameOver()) {
                if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && authData.username().equals(gameData.whiteUsername())  &&
                        game.getBoard().getPiece(makeMoveRequest.getMove().getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE) || (
                        game.getTeamTurn() == ChessGame.TeamColor.BLACK && authData.username().equals(gameData.blackUsername()) &&
                                game.getBoard().getPiece(makeMoveRequest.getMove().getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    //make sure the move is in valid moves
                    if (game.validMoves(makeMoveRequest.getMove().getStartPosition()).contains(makeMoveRequest.getMove())) {
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
                        var notificationMessage = String.format("%s %s was played",
                                convertPositionToString(makeMoveRequest.getMove().getStartPosition()),
                                convertPositionToString(makeMoveRequest.getMove().getEndPosition()));
                        var notification = new Notification(notificationMessage);
                        messageToOtherClients = new Gson().toJson(notification);
                        sessionsManager.sendToOtherClients(makeMoveRequest.getGameID(), makeMoveRequest.getAuthString(), messageToOtherClients);

                        if(gameData.game().isInCheckmate(gameData.game().getTeamTurn())){
                            notificationMessage = String.format("CHECKMATE!!! THE GAME IS OVER");
                            sendNotificationToEveryone(session, makeMoveRequest, gameData, loadGame, notificationMessage, true);
                        }
                        else if(gameData.game().isInCheck(gameData.game().getTeamTurn())){
                            notificationMessage = String.format("CHECK!!!");
                            sendNotificationToEveryone(session, makeMoveRequest, gameData, loadGame, notificationMessage, false);
                        }
                        else if(gameData.game().isInStalemate(gameData.game().getTeamTurn())){
                            notificationMessage = String.format("STALEMATE!!! THE GAME IS OVER");
                            sendNotificationToEveryone(session, makeMoveRequest, gameData, loadGame, notificationMessage, true);
                        }
                    } else {
                        var error = new Error("move is trash brother");
                        session.getRemote().sendString(new Gson().toJson(error));
                    }
                } else {
                    var error = new Error("why you trynna move someone else's piece");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
            } else {
                var error = new Error("sorry mate the games already over");
                session.getRemote().sendString(new Gson().toJson(error));
            }
        }
    }

    private String convertPositionToString(ChessPosition position){
        char[] colLetterList = {'a','b','c','d','e','f','g','h'};
        char colLetter = colLetterList[position.getColumn() - 1];

        return colLetter + "" + position.getRow();
    }

    private void sendNotificationToEveryone(Session session, MakeMove makeMoveRequest, GameData gameData, LoadGame loadGame, String notificationMessage, boolean gameOver) throws DataAccessException, IOException {
        Notification notification;
        String messageToOtherClients;
        gameData.game().setGameOver(gameOver);
        gameDAO.updateGame(gameData);
        notification = new Notification(notificationMessage);
        session.getRemote().sendString(new Gson().toJson(notification));
        messageToOtherClients = new Gson().toJson(notification);
        sessionsManager.sendToOtherClients(makeMoveRequest.getGameID(), makeMoveRequest.getAuthString(), messageToOtherClients);
    }

    private void resign(String message, Session session) throws Exception {
        Resign resignRequest = new Gson().fromJson(message, Resign.class);
        AuthData authData = authDAO.getAuthData(resignRequest.getAuthString());
        GameData gameData = gameDAO.getGame(resignRequest.getGameID());
        //make sure the gameID and the authToken and correct
        if((authData.username() != null) || (gameDAO.getGame(resignRequest.getGameID()) != null)){
            //here we are going to make sure the game isn't already over
            if(authData.username().equals(gameData.whiteUsername() )|| authData.username().equals(gameData.blackUsername())){
                //make sure that no one can resign after gameOver has been set to true
                if(!gameDAO.getGame(resignRequest.getGameID()).game().isGameOver()){
                    //set to over
                    gameData.game().setGameOver(true);
                    //upzate ze game
                    gameDAO.updateGame(gameData);

                    //here we are broadcasting it to everyone
                    var notificationMessage = String.format("%s has resigned: the game has ended", authData.username());
                    var notification = new Notification(notificationMessage);
                    session.getRemote().sendString(new Gson().toJson(notification));
                    var messageToOtherClients = new Gson().toJson(notification);
                    sessionsManager.sendToOtherClients(resignRequest.getGameID(), resignRequest.getAuthString(), messageToOtherClients);
                }
                else {
                    var error = new Error("sorry mate the games already been resigned/over");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
            } else {
                var error = new Error("You cant resign your an observer");
                session.getRemote().sendString(new Gson().toJson(error));
            }
        }
    }

}
