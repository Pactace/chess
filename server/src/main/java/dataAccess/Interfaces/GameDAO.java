package dataAccess.Interfaces;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGame(int id) throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    void joinGame(String playerColor, int id, String username);
    void updateGame(GameData gameData);
    Collection<GameData> listGames() throws DataAccessException;
    void clear() throws DataAccessException;
}