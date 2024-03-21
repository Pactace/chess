package ServerFacade;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private static String authToken = "";

    public ServerFacade(String url) {
        serverUrl = url;
    }

    //pre-login

    public AuthData register(UserData user) throws Exception {
        var path = "/user";
        AuthData authData = this.makeRequest("POST", path, user, AuthData.class);
        authToken = authData.authToken();
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        var path = "/session";
        AuthData authData = this.makeRequest("POST", path, user, AuthData.class);
        authToken = authData.authToken();
        return authData;
    }

    //post-login
    public GameData[] listGames() throws Exception {
        var path = "/game";
        record listGameResponse(GameData[] games) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class);
        return response.games();
    }

    public int createGame(GameData game) throws Exception {
        var path = "/game";
        return this.makeRequest("POST", path, game.gameName(), int.class);
    }

    public void joinOrObserveGame(String playerColor, int gameID) throws Exception {
        var path = "/game";
        record JoinGameData(String playerColor, int gameID) {
        }
        JoinGameData joinGameData = new JoinGameData(playerColor, gameID);

        this.makeRequest("PUT", path, joinGameData, null);
    }

    public void logout() throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, "", null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //somewhere here to store the authToken
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception();
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception();
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
