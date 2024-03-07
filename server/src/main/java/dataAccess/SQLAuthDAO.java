package dataAccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public static void main(String[] args) throws Exception {
        new SQLAuthDAO();
    }
    public AuthData getAuthData(String authToken) {
        return null;
    }

    public AuthData createAuth(AuthData authData) {
        return null;
    }

    public void removeAuth(String authToken) {

    }

    public void clear() {

    }

    void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS auth (
            authToken VARCHAR(255),
            username VARCHAR(255),
            PRIMARY KEY (authToken)
            )""";

            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to get connection");
        }
    }
}
