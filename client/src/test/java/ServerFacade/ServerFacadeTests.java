package ServerFacade;
import dataAccess.SQLAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ServerFacadeTeste {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
    }

    @Test
    void goodRegisterReturnsAuthData() throws Exception {
        UserData newUser = new UserData("test","test","test");
        AuthData authData = serverFacade.register(newUser);

        Assertions.assertNotEquals(null, authData.username());
        Assertions.assertNotEquals(null, authData.authToken());
        serverFacade.clear();
    }

    @Test
    void badRegisterThrowsError() throws Exception {
        UserData newUser = new UserData("test",null,"test");
        Assertions.assertThrows(Exception.class, () ->serverFacade.register(newUser));
        serverFacade.clear();
    }

    @Test
    void goodLoginReturnsAuthData() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        AuthData authData = serverFacade.login(newUser);

        Assertions.assertNotEquals(null, authData.username());
        Assertions.assertNotEquals(null, authData.authToken());
        serverFacade.clear();
    }

    @Test
    void BadLoginThrows() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        UserData badLogin = new UserData("test",null,"test");
        Assertions.assertThrows(Exception.class, () ->serverFacade.login(badLogin));
        serverFacade.clear();
    }

    @Test
    void listOf5GamesReturns5Games() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        serverFacade.createGame("1");
        serverFacade.createGame("1");
        serverFacade.createGame("1");
        serverFacade.createGame("1");
        serverFacade.createGame("1");
        assertEquals(5,serverFacade.listGames().length);
        serverFacade.clear();
    }

    @Test
    void noAuthTokenOnListThrows() throws Exception {
        Assertions.assertThrows(Exception.class, () ->serverFacade.listGames());
        serverFacade.clear();
    }

    @Test
    void createGameReturnsGameID() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        assertEquals(1, serverFacade.createGame("1"));
        serverFacade.clear();
    }

    @Test
    void noAuthTokeOnCreateGameThrowsException() throws Exception {
        Assertions.assertThrows(Exception.class, () ->serverFacade.createGame("Test"));
        serverFacade.clear();
    }

    @Test
    void joinGameWorks() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        serverFacade.createGame("test");
        serverFacade.joinOrObserveGame(1, "WHITE");
        Assertions.assertNotEquals(null, server.gameDAO.getGame(1).whiteUsername());
        serverFacade.clear();
    }

    @Test
    void joinGameThrowsException() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinOrObserveGame(1, "WHITE"));
        serverFacade.clear();
    }

    @Test
    void ObserveGameThrowsException() throws Exception {
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinOrObserveGame(0, "WHITE"));
        serverFacade.clear();
    }

    @Test
    void logoutWorks() throws Exception {
        UserData newUser = new UserData("test","test","test");
        String authToken = serverFacade.register(newUser).authToken();
        serverFacade.logout();
        Assertions.assertNull(server.authDAO.getAuthData(authToken));
        serverFacade.clear();
    }

    @Test
    void noAuthTokenLogoutThrowsException() {
        Assertions.assertThrows(Exception.class, () ->serverFacade.logout());
    }

    @Test
    void clear() {
        Assertions.assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }
}
