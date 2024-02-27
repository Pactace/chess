package model;

import com.google.gson.Gson;

public record AuthData(String authToken, String password, String email) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
