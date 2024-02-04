package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessMove move;

    private ChessPosition[] enemyLocations;
    private ChessPosition[] friendlyLocations;
    private ChessPosition thisKingPostion;
    public ChessGame() { }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //what I am going to do in this one is; cycle through all the moves of the selected piece and if any of them put the king in danger
        //it's an invalid move
        HashSet<ChessMove> validMoves = new HashSet<>();
        if(board.getPiece(startPosition) != null){
            //first we set the selected moves
            ChessPiece selectedPiece = board.getPiece(startPosition);
            //cycle through each of the moves
            for(ChessMove move: selectedPiece.pieceMoves(board,startPosition)){
                //if the king is not in check then add that to the valid moves
                if(!isInCheck(teamTurn)){
                    validMoves.add(move);
                }
            }
        }
        //this either returns an empty set or a set that has been populated
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        this.move = move;
    }

    /**
     * Determines if the given team is in check
     *
     * Implementation: if any of the opposing teams moves include this teams kings current position, then return true
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //for each enemy in the enemyLocations
        for(ChessPosition enemyLocation: enemyLocations){
            //set the enemyPiece that one and go through each of its moves
            ChessPiece enemyPiece = board.getPiece(enemyLocation);
            for(ChessMove move : enemyPiece.pieceMoves(board,enemyLocation)){
                if(move.getEndPosition() == thisKingPostion){
                    //if the location of the move is the same as the thisKingPosition then return true
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //if this team is in check and there are no valid moves for any of this teams pieces
        if(isInCheck(teamTurn)){
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * Implementation: if its this teams turn and they have no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(getTeamTurn() == teamColor){
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
