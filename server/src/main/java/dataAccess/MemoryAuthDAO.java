package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public AuthData getAuthData(String authToken){
        return authTokens.get(authToken);
    }

    public AuthData createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    //this class recieves an authToken and then deletes the authToken from the authTokens
    public void removeAuth(String authToken) {
        authTokens.remove(authToken);
    }

    public void clear(){ authTokens.clear(); }
}
