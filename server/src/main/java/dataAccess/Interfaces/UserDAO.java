package dataAccess.Interfaces;

import dataAccess.DataAccessException;
import model.UserData;
import server.ErrorMessageResponse;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    void clear() throws DataAccessException;
}
