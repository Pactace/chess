package dataAccess.SQL;

import dataAccess.DataAccessException;
import dataAccess.Interfaces.AuthDAO;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
        //AuthData user = new AuthData(UUID.randomUUID().toString(),"Fart even though its not in the database"); createAuth(user);
        //getAuthData("21f05de2-9e65-4471-95ca-a83efd1fd7c1");
        //removeAuth("c3cec397-b9e7-4bda-820a-0119868a41cc");
        //clear();
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

                        System.out.printf("authToken: %s, username: %s", foundAuthToken, username);
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
    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
