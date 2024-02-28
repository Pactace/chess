package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import model.GameData;
import model.AuthData;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final UserService userService;
    UserDAO userDAO = new UserDAO();
    AuthDAO authDAO = new AuthDAO();

    public Server(){
        userService = new UserService(userDAO, authDAO);
    }

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        //first we parse the user data
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        //then we set the authData to the same authData we get form the register function
        AuthData authData = userService.register(userData);
        res.status(200);
        //finally we return the authToken
        return new Gson().toJson(authData);
    }

    private Object login(Request req, Response res) {
        //first we parse the user data
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        //then we set the authData to the same authData we get form the register function
        AuthData authData = userService.login(userData);
        //finally we return the authToken
        return new Gson().toJson(authData);
    }

    private Object logout(Request req, Response res) {
        //first we get the authToken
        String authToken = req.headers(":authorization");

        //here we log out
        userService.logout(authToken);

        //then we just return the response
        return res.status();
    }



}
