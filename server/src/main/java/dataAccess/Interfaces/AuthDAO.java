package dataAccess.Interfaces;

import dataAccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    AuthData getAuthData(String authToken) throws DataAccessException;
    AuthData createAuth(AuthData authData) throws DataAccessException;
    void removeAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
