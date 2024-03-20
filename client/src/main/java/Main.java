import chess.*;

public class Main {
    //ask the user a question and then we just react to it. Main program responisbility
    //Each menu should be responsible for setting its own set of commands.
    //navigator class goes from place to place in the UIs
    public static void main(String[] args) throws Exception{
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ Welcome to the 240 Chess Client: Type 'help' to get started");
    }
}