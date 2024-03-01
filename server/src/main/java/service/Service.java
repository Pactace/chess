package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.JoinGameRequestData;

import java.util.Collection;
import java.util.UUID;

public class Service {
    public final UserDAO userDAO;
    public final AuthDAO authDAO;
    public final GameDAO gameDAO;
    public Service(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData register(UserData user) throws AlreadyTakenException {
        //first we get the user by its username to make sure it doesn't already exist
        if(userDAO.getUser(user.username()) == null){
            //here we are going to create a user
            userDAO.createUser(user);

            //here we are going to create an authToken and then put that in the createAuth
            String authToken = UUID.randomUUID().toString();
            return authDAO.createAuth(new AuthData(authToken, user.username()));
        }
        else{
            throw new AlreadyTakenException();
        }
    }
    public AuthData login(UserData user) throws Exception {

        //here we are going to check if the user exists if he does
        if(userDAO.getUser(user.username()) != null){

            //we are going to check if the user password matches the password on record
            if(!user.password().equals(userDAO.getUser(user.username()).password())){
                throw new UnauthorizedException();
            }

            //we are going to give the auth token
            String authToken = UUID.randomUUID().toString();
            return authDAO.createAuth(new AuthData(authToken, user.username()));
        }
        else{
            throw new UnauthorizedException();
        }
    }


    public void logout(String authToken) throws UnauthorizedException {
        //if the authtoken doesn't exist in the database
        if(authDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        authDAO.removeAuth(authToken);
    }

    public GameData createGame(String authToken, String gameName) throws UnauthorizedException {
        //if there is no authToken matching this one then throw an unauthorized error
        if(authDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }

        //here we create the Data in the game DAO and return the gameID
        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, JoinGameRequestData joinGameRequestData) throws Exception {
        //if the authToken is crap throw an error
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException();
        }

        if(gameDAO.getGame(joinGameRequestData.gameID()) != null) {
            //if the color is alreadyTaken throw an Already Taken error
            if ((joinGameRequestData.playerColor().equals("WHITE") && gameDAO.getGame(joinGameRequestData.gameID()).whiteUsername() != null) ||
                    (joinGameRequestData.playerColor().equals("BLACK") && gameDAO.getGame(joinGameRequestData.gameID()).blackUsername() != null)) {
                throw new AlreadyTakenException();
            }
        }
        else{
            throw new BadRequestException();
        }

        if(joinGameRequestData.playerColor().equals("BLACK") || joinGameRequestData.playerColor().equals("WHITE")){
            //if all those tests have passed then hit em with the username
            System.out.println(joinGameRequestData.playerColor() + " " + joinGameRequestData.gameID() + " " + authDAO.getAuthData(authToken).username());
            gameDAO.joinGame(joinGameRequestData.playerColor(), joinGameRequestData.gameID(), authDAO.getAuthData(authToken).username());
        }


    }

    public Collection<GameData> listGames (String authToken) throws UnauthorizedException {
        //if there is no authToken matching this one then throw an unauthorized error
        if(authDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        //here we create the Data in the game DAO and return the gameID
        return gameDAO.listGames();
    }
    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
