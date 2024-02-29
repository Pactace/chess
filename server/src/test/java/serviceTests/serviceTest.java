package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AlreadyTakenException;
import service.UnauthorizedException;
import service.Service;

class serviceTest {
    //add the userDAO and authDAO make sure that they are reset before every test
    UserDAO userDAO = new UserDAO();
    AuthDAO authDAO = new AuthDAO();
    GameDAO gameDAO = new GameDAO();
    Service Service = new Service(userDAO, authDAO, gameDAO);

    @BeforeEach
    void setup(){
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        Service Service = new Service(userDAO, authDAO, gameDAO);
    }
    @Test
    void registerUserAddsToUserDAO() throws AlreadyTakenException {
        //For this test to work we just register a new user and make sure that it populates correctly
        UserData testUser = new UserData("TestUsername", "TestPassword", "TestEmail");
        Service.register(testUser);

        Assertions.assertEquals(testUser , Service.userDAO.getUser(testUser.username()));
    }
    @Test
    void registerThrowsAlreadyTaken() {
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
    void clear() {
    }
}