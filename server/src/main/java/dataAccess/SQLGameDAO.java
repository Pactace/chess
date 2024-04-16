package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.Interfaces.GameDAO;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public static void main(String[] args) throws Exception {
        new SQLGameDAO();
    }
    public GameData getGame(int id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName, game FROM game WHERE gameId=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        if(gameName == null){
            throw new DataAccessException("cannot insert null as game name");
        }
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame newChessGame = new ChessGame();
        var json = new Gson().toJson(newChessGame);
        var id = executeUpdate(statement, null, null, gameName, json);
        return new GameData(id, null,null, gameName, newChessGame);
    }

    public void joinGame(String playerColor, int id, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if(getGame(id) == null){
                throw new DataAccessException("invalid id");
            }

            if(playerColor.equals("WHITE")) {
                try (var preparedStatement = conn.prepareStatement("UPDATE game SET whiteUsername=? WHERE gameId=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, id);
                    preparedStatement.executeUpdate();}
            }
            if(playerColor.equals("BLACK")) {
                try (var preparedStatement = conn.prepareStatement("UPDATE game SET blackUsername=? WHERE gameId=?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, id);
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameData updateGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            if(getGame(gameData.gameID()) == null){
                throw new DataAccessException("invalid id");
            }

            try (var preparedStatement = conn.prepareStatement(
                    "UPDATE game SET whiteUsername=?, blackUsername=?, game=? WHERE gameId=?")) {
                    preparedStatement.setString(1, gameData.whiteUsername());
                    preparedStatement.setString(2, gameData.blackUsername());
                    preparedStatement.setString(3, new Gson().toJson(gameData.game()));
                    preparedStatement.setInt(4, gameData.gameID());
                    preparedStatement.executeUpdate();}

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return gameData;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("gameId");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("game");
        var game = new Gson().fromJson(gameJson, ChessGame.class);
        GameData fullGame = new GameData(id, whiteUsername ,blackUsername ,gameName ,game);
        return fullGame;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS game (
            gameId INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255),
            game VARCHAR(255),
            PRIMARY KEY (gameId)
            )""";

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to get connection");
        }
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
        return 0;
    }
}
