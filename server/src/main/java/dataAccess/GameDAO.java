package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGame(int id);
    GameData createGame(String gameName);
    void joinGame(String playerColor, int id, String username);
    void updateGame(GameData gameData);
    Collection<GameData> listGames();
    void clear();
}
