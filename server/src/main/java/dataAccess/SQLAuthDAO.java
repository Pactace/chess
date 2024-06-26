package dataAccess;

import dataAccess.Interfaces.AuthDAO;
import model.AuthData;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO extends SQLExecuteUpdate implements AuthDAO {
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public static void main(String[] args) throws Exception {
        new SQLAuthDAO();
    }
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var foundAuthToken = rs.getString("authToken");
                        var username = rs.getString("username");

                        return new AuthData(foundAuthToken, username);
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(String.format("unable to query database: %s, %s", e.getMessage()));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable get connection: %s, %s", e.getMessage()));
        }
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        if(authData.authToken() == null){
            throw new DataAccessException("cannot have null authtoken");
        }
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        //here we are going to update it
        executeUpdate(statement, authData.authToken(), authData.username());
        return authData;
    }

    public void removeAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
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
