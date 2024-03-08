package service;

import dataAccess.Memory.MemoryAuthDAO;
import dataAccess.Memory.MemoryGameDAO;
import dataAccess.Memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.JoinGameRequestData;

import java.util.Collection;
import java.util.UUID;

public class Service {
    public final MemoryUserDAO memoryUserDAO;
    public final MemoryAuthDAO memoryAuthDAO;
    public final MemoryGameDAO memoryGameDAO;
    public Service(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO, MemoryGameDAO memoryGameDAO){
        this.memoryUserDAO = memoryUserDAO;
        this.memoryAuthDAO = memoryAuthDAO;
        this.memoryGameDAO = memoryGameDAO;
    }

    public AuthData register(UserData user) throws AlreadyTakenException {
        //first we get the user by its username to make sure it doesn't already exist
        if(memoryUserDAO.getUser(user.username()) == null){
            //here we are going to create a user
            memoryUserDAO.createUser(user);

            //here we are going to create an authToken and then put that in the createAuth
            String authToken = UUID.randomUUID().toString();
            return memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        }
        else{
            throw new AlreadyTakenException();
        }
    }
    public AuthData login(UserData user) throws Exception {

        //here we are going to check if the user exists if he does
        if(memoryUserDAO.getUser(user.username()) != null){

            //we are going to check if the user password matches the password on record
            if(!user.password().equals(memoryUserDAO.getUser(user.username()).password())){
                throw new UnauthorizedException();
            }

            //we are going to give the auth token
            String authToken = UUID.randomUUID().toString();
            return memoryAuthDAO.createAuth(new AuthData(authToken, user.username()));
        }
        else{
            throw new UnauthorizedException();
        }
    }


    public void logout(String authToken) throws UnauthorizedException {
        //if the authtoken doesn't exist in the database
        if(memoryAuthDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        memoryAuthDAO.removeAuth(authToken);
    }

    public GameData createGame(String authToken, String gameName) throws UnauthorizedException {
        //if there is no authToken matching this one then throw an unauthorized error
        if(memoryAuthDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }

        //here we create the Data in the game DAO and return the gameID
        return memoryGameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, JoinGameRequestData joinGameRequestData) throws Exception {
        //if the authToken is crap throw an error
        if (memoryAuthDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException();
        }

        if (memoryGameDAO.getGame(joinGameRequestData.gameID()) == null) {
            throw new BadRequestException();
        }


        if (joinGameRequestData.playerColor() != null) {
            //if the color is alreadyTaken throw an Already Taken error
            if ((joinGameRequestData.playerColor().equals("WHITE") && memoryGameDAO.getGame(joinGameRequestData.gameID()).whiteUsername() != null) ||
                    (joinGameRequestData.playerColor().equals("BLACK") && memoryGameDAO.getGame(joinGameRequestData.gameID()).blackUsername() != null)) {
                throw new AlreadyTakenException();
            }
            if(joinGameRequestData.playerColor().equals("BLACK") || joinGameRequestData.playerColor().equals("WHITE")){
                //if all those tests have passed then hit em with the username
                memoryGameDAO.joinGame(joinGameRequestData.playerColor(), joinGameRequestData.gameID(), memoryAuthDAO.getAuthData(authToken).username());
            }
        }
    }

    public Collection<GameData> listGames (String authToken) throws UnauthorizedException {
        //if there is no authToken matching this one then throw an unauthorized error
        if(memoryAuthDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        //here we create the Data in the game DAO and return the gameID
        return memoryGameDAO.listGames();
    }
    public void clear() {
        memoryUserDAO.clear();
        memoryAuthDAO.clear();
        memoryGameDAO.clear();
    }
}
