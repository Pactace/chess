package webSocketMessages.userCommands;

import chess.ChessMove;

public class Leave extends UserGameCommand{
    private Integer gameID;

    public Leave(String authToken, Integer gameID) {
        super(authToken, CommandType.LEAVE);
        this.gameID = gameID;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }
}
