package model;

import chess.ChessGame;
import com.google.gson.*;

/**
 * Data Model class that stores the information for the Chess Game
 * @param gameID
 * @param whiteUsername
 * @param blackUsername
 * @param gameName
 * @param game
 */

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
