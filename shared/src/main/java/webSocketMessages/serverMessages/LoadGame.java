package webSocketMessages.serverMessages;

import chess.ChessBoard;

public class LoadGame extends ServerMessage {
    private final ChessBoard game;
    public LoadGame(ChessBoard game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessBoard getGame() {
        return game;
    }
}
