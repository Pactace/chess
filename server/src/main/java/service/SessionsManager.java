package service;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsManager {
    private Map<Integer, Map<String, Session>> sessionMap = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, String authToken, Session session){
        Map<String, Session> sessionsForGame = getSessionsForGame(gameID);
        if(sessionsForGame == null){
            sessionsForGame = new ConcurrentHashMap<>();
        }
        sessionsForGame.put(authToken, session);
        sessionMap.put(gameID, sessionsForGame);
    }
    public void removeSessionFromGame(int gameID, String authToken){
        Map<String, Session> sessionsForGame = getSessionsForGame(gameID);
        if(sessionsForGame != null){
            sessionsForGame.remove(authToken);
        }
    }
    public Map<String, Session> getSessionsForGame(int gameID){
        return sessionMap.get(gameID);
    }
    public void sendToOtherClients(int gameID, String authToken, String message) throws IOException {
        Set<String> keys = getSessionsForGame(gameID).keySet();
        Map<String, Session> sessionsForGame = getSessionsForGame(gameID);
        for(String key : keys){
            if(!key.equals(authToken)){
                Session otherClientSession = sessionsForGame.get(key);
                otherClientSession.getRemote().sendString(message);
            }
        }
    }

}
