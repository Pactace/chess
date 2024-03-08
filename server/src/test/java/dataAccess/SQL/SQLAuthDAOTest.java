package dataAccess.SQL;

import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    SQLAuthDAO sqlAuthDAO;

    {
        try {
            sqlAuthDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        sqlAuthDAO.clear();
    }

    @Test
    void getAuthDataReturnsCorrectUsername() throws DataAccessException {
        sqlAuthDAO.createAuth(new AuthData("testToken", "testUsername"));
        Assertions.assertEquals("testUsername",sqlAuthDAO.getAuthData("testToken").username());
    }
    @Test
    void getAuthDataReturnsNull() throws DataAccessException {
        sqlAuthDAO.createAuth(new AuthData("testToken", "testUsername"));
        Assertions.assertNull(sqlAuthDAO.getAuthData("falseToken"));
    }

    @Test
    void createAuthShowsUp() throws DataAccessException {
        sqlAuthDAO.createAuth(new AuthData("testToken", "testUsername"));
        Assertions.assertEquals("testUsername",sqlAuthDAO.getAuthData("testToken").username());
    }

    @Test
    void createAuthNullTokenThrows() throws DataAccessException {
        sqlAuthDAO.createAuth(new AuthData("testToken", "testUsername"));

        Assertions.assertThrows(DataAccessException.class, () ->
                sqlAuthDAO.createAuth(new AuthData(null,"Test User")));
    }

    @Test
    void removeAuthWhenCorrectToken() throws DataAccessException {
        AuthData authData = new AuthData("testToken", "testUsername");
        sqlAuthDAO.createAuth(authData);
        Assertions.assertNotEquals(null, sqlAuthDAO.getAuthData("testToken"));
        sqlAuthDAO.removeAuth("testToken");
        Assertions.assertNull(sqlAuthDAO.getAuthData("testToken"));
    }
    @Test
    void dontRemoveAuthWhenFalseToken() throws DataAccessException {
        AuthData authData = new AuthData("testToken", "testUsername");
        sqlAuthDAO.createAuth(authData);
        Assertions.assertNotEquals(null, sqlAuthDAO.getAuthData("testToken"));
        sqlAuthDAO.removeAuth("fakeToken");
        Assertions.assertNotEquals(null,sqlAuthDAO.getAuthData("testToken"));
    }

    @Test
    void clear() {
    }
}