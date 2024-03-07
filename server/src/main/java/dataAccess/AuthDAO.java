package dataAccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuthData(String authToken);
    AuthData createAuth(AuthData authData);
    void removeAuth(String authToken);
    void clear();
}
