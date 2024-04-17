package websocket;

import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public interface GameHandler {
    void notify(Notification serverMessage);
    void error(Error serverMessage);
    void loadGame(LoadGame serverMessage);


}
