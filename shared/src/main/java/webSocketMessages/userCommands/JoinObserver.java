package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand{
    private Integer gameID;

    public JoinObserver(String authToken, Integer gameID) {
        super(authToken, CommandType.JOIN_OBSERVER);
        this.gameID = gameID;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }
}
