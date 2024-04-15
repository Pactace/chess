package service;

import dataAccess.DataAccessException;
import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import dataAccess.Memory.MemoryAuthDAO;
import dataAccess.Memory.MemoryGameDAO;
import dataAccess.Memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public AuthData register(UserData user) throws Exception {
        //first we get the user by its username to make sure it doesn't already exist
        if(userDAO.getUser(user.username()) == null){
            //first we are going to reinitialize user so that he has a hashed password
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user = new UserData(user.username(),encoder.encode(user.password()),user.email());
            System.out.println("the encoded password when register happens: " + user.password());
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
            //were going to encode the password
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            //we are going to check if the user password matches the encoded password on record
            if(!encoder.matches(user.password(), userDAO.getUser(user.username()).password())) {
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


    public void logout(String authToken) throws Exception {
        //if the authtoken doesn't exist in the database
        if(authDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        authDAO.removeAuth(authToken);
    }

    public GameData createGame(String authToken, String gameName) throws Exception {
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

        if (gameDAO.getGame(joinGameRequestData.gameID()) == null) {
            throw new BadRequestException();
        }


        if (joinGameRequestData.playerColor() != null) {
            //if the color is alreadyTaken throw an Already Taken error
            if ((joinGameRequestData.playerColor().equals("WHITE") && gameDAO.getGame(joinGameRequestData.gameID()).whiteUsername() != null) ||
                    (joinGameRequestData.playerColor().equals("BLACK") && gameDAO.getGame(joinGameRequestData.gameID()).blackUsername() != null)) {
                throw new AlreadyTakenException();
            }
            if(joinGameRequestData.playerColor().equals("BLACK") || joinGameRequestData.playerColor().equals("WHITE")){
                //if all those tests have passed then hit em with the username
                gameDAO.joinGame(joinGameRequestData.playerColor(), joinGameRequestData.gameID(), authDAO.getAuthData(authToken).username());
            }
        }
    }

    public Collection<GameData> listGames (String authToken) throws Exception {
        //if there is no authToken matching this one then throw an unauthorized error
        if(authDAO.getAuthData(authToken) == null){
            throw new UnauthorizedException();
        }
        //here we create the Data in the game DAO and return the gameID
        return gameDAO.listGames();
    }
    public void joinPlayer(String username){}
    public void joinObserver(){}
    public void makeMove(){}
    public void leaveGame(){}
    public void resignGame(){
    }
    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
