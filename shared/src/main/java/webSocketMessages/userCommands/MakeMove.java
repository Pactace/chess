package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    private Integer gameID;
    private ChessMove move;
    private UserGameCommand.CommandType commandType;

    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(authToken);
        commandType = UserGameCommand.CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public UserGameCommand.CommandType getCommandType() {
        return commandType;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
