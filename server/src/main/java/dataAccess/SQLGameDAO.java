package dataAccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class SQLGameDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public static void main(String[] args) throws Exception {
        new SQLGameDAO();
    }
    public GameData getGame(int id) {
        return null;
    }

    public GameData createGame(String gameName) {
        return null;
    }

    public void joinGame(String playerColor, int id, String username) {

    }

    public void updateGame(GameData gameData) {
    }

    public Collection<GameData> listGames() {
        return null;
    }

    public void clear() {

    }

    void configureDatabase() throws DataAccessException {
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
}
