package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//We need to create a createGame, joinGame, listGames
public class GameDAO {
    //first we create a hashmap that will store the user data
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int gameID = 0;

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

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void clear(){
        games.clear();
    }
}
