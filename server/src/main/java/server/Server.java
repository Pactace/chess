package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.GameData;
import model.UserData;
import model.AuthData;
import requests.JoinGameRequestData;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.Service;
import spark.*;

import java.util.Collection;

public class Server {
    private final Service service;
    MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    public Server(){
        service = new Service(memoryUserDAO, memoryAuthDAO, memoryGameDAO);
    }

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) {
        try{
            service.clear();
            return new Gson().toJson(new ErrorMessageResponse("["+ res.status() + "]"));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }

    }

    private Object register(Request req, Response res) {
        //first we parse the user data
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        if(userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            BadRequestException errorMessage = new BadRequestException();
            return new Gson().toJson(new ErrorMessageResponse(errorMessage.getMessage()));
        }

        //then we set the authData to the same authData we get form the register function
        try{
            AuthData authData = service.register(userData);
            res.status(200);
            //finally we return the authToken
            return new Gson().toJson(authData);
        }
        catch(AlreadyTakenException e){
            res.status(403);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }

    private Object login(Request req, Response res) {
        //first we parse the user data
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        try{
            //then we set the authData to the same authData we get form the register function
            AuthData authData = service.login(userData);
            //finally we return the authToken
            res.status(200);
            return new Gson().toJson(authData);
        }
        catch(UnauthorizedException e){
            res.status(401);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }

    private Object logout(Request req, Response res) {
        //first we get the authToken
        String authToken = req.headers("Authorization");

        try{
            service.logout(authToken);
            return new Gson().toJson(new ErrorMessageResponse("["+ res.status() + "]"));
        }
        catch(UnauthorizedException e){
            res.status(401);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }

    private Object createGame(Request req, Response res) {
        //first we get the game name and authToken
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);
        String authToken = req.headers("Authorization");

        String gameName = gameData.gameName();
        //if the game name is null then we blast it with a bad request
        if(gameName == null){
            res.status(400);
            BadRequestException errorMessage = new BadRequestException();
            return new Gson().toJson(new ErrorMessageResponse(errorMessage.getMessage()));
        }

        //here we try to set the gameData to what gets passed back from our service
        try{
            GameData newGameData = service.createGame(authToken, gameName);
            res.status(200);
            //finally we return the authToken
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("gameID", newGameData.gameID());
            // Convert the JSON object to JSON format
            return new Gson().toJson(jsonResponse);
        }
        catch(UnauthorizedException e){
            res.status(401);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }

    private Object listGames(Request req, Response res) {
        //first we get the game name and authToken
        String authToken = req.headers("Authorization");


        //here we try to set the gameData to what gets passed back from our service
        try{
            Collection<GameData> games = service.listGames(authToken);
            res.status(200);
            JsonArray gamesArray = new JsonArray();
            for (GameData game : games) {
                JsonObject gameJson = new JsonObject();
                gameJson.addProperty("gameID", game.gameID());
                gameJson.addProperty("whiteUsername", game.whiteUsername());
                gameJson.addProperty("blackUsername", game.blackUsername());
                gameJson.addProperty("gameName", game.gameName());
                gamesArray.add(gameJson);
            }
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.add("games", gamesArray);
            return new Gson().toJson(jsonResponse);
        }
        catch(UnauthorizedException e){
            res.status(401);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }

    private Object joinGame(Request req, Response res) {
        //first we get the game name and authToken
        String authToken = req.headers("Authorization");
        JoinGameRequestData joinGameRequestData = new Gson().fromJson(req.body(), JoinGameRequestData.class);

        //here we try to set the gameData to what gets passed back from our service
        try{
            service.joinGame(authToken, joinGameRequestData);
            res.status(200);
            return "{}";
        }
        catch(BadRequestException e){
            res.status(400);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(UnauthorizedException e){
            res.status(401);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(AlreadyTakenException e){
            res.status(403);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
        catch(Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorMessageResponse(e.getMessage()));
        }
    }



}
