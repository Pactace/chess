package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    private Integer gameID;

    public Resign(String authToken, Integer gameID) {
        super(authToken, CommandType.RESIGN);
        this.gameID = gameID;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }
}
