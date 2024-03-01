package requests;

import com.google.gson.Gson;

public record JoinGameRequestData(String playerColor, int gameID) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
