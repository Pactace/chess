package dataAccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UserDAO {
    //first we create a hashmap that will store the user data
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username) {
        return users.get(username);
    }
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    public void clear(){
        users.clear();
    }
}
