package dataAccess.Memory;

import chess.ChessGame;
import dataAccess.Interfaces.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

//We need to create a createGame, joinGame, listGames
public class MemoryGameDAO implements GameDAO {
    //first we create a hashmap that will store the user data
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int gameID = 1;

    public GameData getGame(int id){
        return games.get(id);
    }

    //it returns the gameData, so we can get the gameID
    public GameData createGame(String gameName) {
        GameData gameData = new GameData(gameID,null,null, gameName, new ChessGame());
        games.put(gameData.gameID(), gameData);

        //increment the gameID and push the return the gameDATA
        gameID++;
        return gameData;
    }

    public void joinGame(String playerColor, int id, String username)
    {
        //make a new gameData taking everything from the previous gameData and modifying the playerColor of the parameter
        GameData oldGame = getGame(id);

        GameData updatedGame = null;

        if(playerColor.equals("WHITE")) {
            updatedGame = new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }
        if(playerColor.equals("BLACK")) {
            updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }

        //here we update the game
        updateGame(updatedGame);
    }

    public GameData updateGame(GameData gameData){
        games.replace(gameData.gameID(), gameData);
        return gameData;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void clear(){
        gameID = 1;
        games.clear();
    }
}
