package dataAccess;

import model.UserData;
import server.ErrorMessageResponse;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData user);
    void clear();
}
