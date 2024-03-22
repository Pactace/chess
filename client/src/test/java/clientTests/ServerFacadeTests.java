package clientTests;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @Test
    void successfulRegisterDoesNotThrowError() {
    }


    void badRegisterThrowsError() {
    }


    @Test
    void login() {
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void joinOrObserveGame() {
    }

    @Test
    void logout() {
    }

    @Test
    void clear() {
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
