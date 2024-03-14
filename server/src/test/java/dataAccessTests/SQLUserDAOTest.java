package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    SQLUserDAO sqlUserDAO;

    {
        try {
            sqlUserDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        sqlUserDAO.clear();
    }

    @Test
    void getRightUserWhenCreated() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("Test User", "Test Password", "Test Email"));

        assertNotEquals(null, sqlUserDAO.getUser("Test User"));
    }

    @Test
    void nonExistentUserShouldReturnNull() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("Test User", "Test Password", "Test Email"));

        assertEquals(null, sqlUserDAO.getUser("Non Existent User"));
    }

    @Test
    void createUserShowsUp() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("Test User", "Test Password", "Test Email"));

        assertNotEquals(null, sqlUserDAO.getUser("Test User"));
    }

    @Test
    void createDuplicateUserThrowsErrors() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("Test User", "Test Password", "Test Email"));
        Assertions.assertThrows(DataAccessException.class, () ->
                sqlUserDAO.createUser(new UserData("Test User", "Test Password2", "Test Email2")));
    }

    @Test
    void clear() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("Test User", "Test Password", "Test Email"));
        sqlUserDAO.clear();

        assertEquals(null, sqlUserDAO.getUser("Test User"));
    }
}