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
    public static void init() {
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
    }

    @Test
    void badRegisterThrowsError(){
        UserData newUser = new UserData("test",null,"test");
        Assertions.assertThrows(Exception.class, () ->serverFacade.register(newUser));
    }

    @Test
    void goodLoginReturnsAuthData() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        AuthData authData = serverFacade.login(newUser);

        Assertions.assertNotEquals(null, authData.username());
        Assertions.assertNotEquals(null, authData.authToken());
    }

    @Test
    void BadLoginThrows() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        UserData badLogin = new UserData("test",null,"test");
        Assertions.assertThrows(Exception.class, () ->serverFacade.login(badLogin));
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
        Assertions.assertEquals(5,serverFacade.listGames().length);
    }

    @Test
    void noAuthTokenOnListThrows() throws Exception {
        Assertions.assertThrows(Exception.class, () ->serverFacade.listGames());
    }

    @Test
    void createGameReturnsGameID() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        Assertions.assertEquals(1, serverFacade.createGame("1"));
    }

    @Test
    void noAuthTokeOnCreateGameThrowsException() throws Exception {
        Assertions.assertThrows(Exception.class, () ->serverFacade.createGame("Test"));
    }

    @Test
    void joinGameWorks() throws Exception {
        UserData newUser = new UserData("test","test","test");
        serverFacade.register(newUser);
        serverFacade.createGame("test");
        serverFacade.joinOrObserveGame(1, "WHITE");
        Assertions.assertNotEquals(null, server.gameDAO.getGame(1).whiteUsername());
    }

    @Test
    void joinGameThrowsException() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinOrObserveGame(1, "WHITE"));
    }

    @Test
    void ObserveGameThrowsException() {
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinOrObserveGame(0, "WHITE"));
    }

    @Test
    void logoutWorks() throws Exception {
        UserData newUser = new UserData("test","test","test");
        String authToken = serverFacade.register(newUser).authToken();
        serverFacade.logout();
        Assertions.assertNull(server.authDAO.getAuthData(authToken));
    }

    @Test
    void noAuthTokenLogoutThrowsException() {
        Assertions.assertThrows(Exception.class, () ->serverFacade.logout());
    }

    @Test
    void clear(){

    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
        serverFacade.clear();
    }
}