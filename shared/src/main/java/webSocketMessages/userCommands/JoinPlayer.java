package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand{
    private Integer gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken, CommandType.JOIN_PLAYER);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
