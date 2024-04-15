package websocket;

import spark.Session;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;

public class WebSocketFacade extends Endpoint implements MessageHandler {

    public Session session;
    public GameHandler gameHandler;


    @Override
    public void onOpen(javax.websocket.Session session, EndpointConfig endpointConfig) {}
}
