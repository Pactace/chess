package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    private Integer gameID;
    private UserGameCommand.CommandType commandType;

    public Resign(String authToken, Integer gameID) {
        super(authToken);
        commandType = UserGameCommand.CommandType.RESIGN;
        this.gameID = gameID;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }
}
