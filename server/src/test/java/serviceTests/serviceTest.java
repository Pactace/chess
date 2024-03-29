package serviceTests;

import dataAccess.Interfaces.AuthDAO;
import dataAccess.Interfaces.GameDAO;
import dataAccess.Interfaces.UserDAO;
import dataAccess.Memory.MemoryAuthDAO;
import dataAccess.Memory.MemoryGameDAO;
import dataAccess.Memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.AlreadyTakenException;
import service.UnauthorizedException;
import service.Service;
import requests.JoinGameRequestData;

class serviceTest {
    //add the userDAO and authDAO make sure that they are reset before every test
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    Service Service = new Service(userDAO, authDAO, gameDAO);

    @BeforeEach
    void setup(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        Service = new Service(userDAO, authDAO, gameDAO);
    }
    @Test
    void registerUserAddsToUserDAO() throws Exception {
        //For this test to work we just register a new user and make sure that it populates correctly
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        Service.register(testUser);

        Assertions.assertEquals(testUser.username() , Service.userDAO.getUser(testUser.username()).username());
    }
    @Test
    void registerThrowsAlreadyTaken() throws Exception {
        //for this exception we add a user into the database first and make sure that it throws an already taken exception
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        Service.userDAO.createUser(testUser);

        Assertions.assertThrows(AlreadyTakenException.class, () ->  Service.register(testUser) );
    }

    @Test
    void loginAddsAuthTokenForUser() throws Exception {
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);
        AuthData authData = Service.login(testUser);

        //making sure that the data exists when you log in
        Assertions.assertNotEquals(Service.authDAO.getAuthData(authData.authToken()) , null);
    }

    @Test
    void loginThrowsUnauthorizedWhenWrongPasswordIsUsed() throws Exception {
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);

        UserData fakeTestUser = new UserData("TestUsername", "TestPassworld", "TestEmail");


        Assertions.assertThrows(UnauthorizedException.class, () ->  Service.login(fakeTestUser) );
    }

    @Test
    void logoutRemovesAuthTokenForUserAfterRegister() throws Exception{
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);

        //making sure that the authtoken no longer exists when its looked up
        Assertions.assertEquals(Service.authDAO.getAuthData(authToken) , null);
    }

    @Test
    void logoutRemovesAuthTokenForUserAfterLogin() throws Exception{
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);
        AuthData authData = Service.login(testUser);
        Service.logout(authData.authToken());

        //making sure that the authtoken no longer exists when its looked up
        Assertions.assertEquals(Service.authDAO.getAuthData(authData.authToken()) , null);
    }

    @Test
    void createGameAppearsInGamesList() throws Exception{
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.createGame(authToken, "TestGame");

        //after we create make sure that the game list isnt null
        Assertions.assertNotEquals(Service.gameDAO.listGames(), null);
    }

    @Test
    void createGameThrowsUnauthorizedException() throws Exception{
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);

        Assertions.assertThrows(UnauthorizedException.class, ()-> Service.createGame(authToken, "TestGame"));
    }

    @Test
    void joinGameWhiteSuccess() throws Exception {
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        int gameID = Service.createGame(authToken, "TestGame").gameID();
        JoinGameRequestData testJoinGameRequest = new JoinGameRequestData("WHITE",gameID);
        Service.joinGame(authToken, testJoinGameRequest);

        Assertions.assertNotEquals(Service.gameDAO.getGame(gameID).whiteUsername(), null);
    }

    @Test
    void joinGameThrowsUnauthorizedException() throws Exception {
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        int gameID = Service.createGame(authToken, "TestGame").gameID();
        JoinGameRequestData testJoinGameRequest = new JoinGameRequestData("WHITE",gameID);
        Service.logout(authToken);

        Assertions.assertThrows(UnauthorizedException.class, ()-> Service.joinGame(authToken, testJoinGameRequest));
    }

    @Test
    void listGamesCreatesFiveGames() throws Exception {
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.createGame(authToken, "TestGame1");
        Service.createGame(authToken, "TestGame2");
        Service.createGame(authToken, "TestGame3");
        Service.createGame(authToken, "TestGame4");
        Service.createGame(authToken, "TestGame5");

        Assertions.assertEquals(Service.listGames(authToken).size(), 5);
    }

    @Test
    void listGamesThrowsUnauthorizedException() throws Exception{
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        String authToken = Service.register(testUser).authToken();
        Service.logout(authToken);

        Assertions.assertThrows(UnauthorizedException.class, ()-> Service.listGames(authToken));
    }

    @Test
    void clear() {
        Assertions.assertDoesNotThrow(() -> Service.clear());
    }
}