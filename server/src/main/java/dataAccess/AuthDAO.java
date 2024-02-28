package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public AuthData createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
        System.out.println(authTokens);
        return authData;
    }

    //this class recieves an authToken and then deletes the authToken from the authTokens
    public void removeAuth(String authToken) {
        authTokens.remove(authToken);
        System.out.println(authTokens);
    }

    public void clear(){ authTokens.clear(); }
}
