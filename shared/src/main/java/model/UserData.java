package model;

import com.google.gson.*;

/**
 * Data Model Class that is made for the UserData
 * @param username
 * @param password
 * @param email
 */

public record UserData(String username, String password, String email) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
