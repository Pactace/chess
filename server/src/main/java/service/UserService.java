package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService (UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        //first we get the user by its username to make sure it doesn't already exist
        if(userDAO.getUser(user.username()) == null){
            //here we are going to create a user
            userDAO.createUser(user);

            //here we are going to create an authToken and then put that in the createAuth
            String authToken = UUID.randomUUID().toString();
            return authDAO.createAuth(new AuthData(authToken, user.username()));
        }
        return null;
    }
    public AuthData login(UserData user) {
        //here we are going to check if the user exists if he does
        if(userDAO.getUser(user.username()) != null){
            //we are going to give the auth token
            String authToken = UUID.randomUUID().toString();
            return authDAO.createAuth(new AuthData(authToken, user.username()));
        }
        //if nothing return null
        return null;
    }


    public void logout(String authToken) {
        authDAO.removeAuth(authToken);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
