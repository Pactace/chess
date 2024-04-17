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
    private boolean gameOver = false;



    public ChessGame() {
    }

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
                //first we are going to make a backup board
                TeamColor initialTeamTurn = teamTurn;
                ChessBoard backupBoard = deepCopyBoard(board);

                //then we are going to start mucking about the with the modifications
                teamTurn = selectedPiece.getTeamColor();

                //next we are going to make the move
                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), selectedPiece);

                //next we are going to only add it if it's not in check
                if(!isInCheck(teamTurn)){
                    validMoves.add(move);
                }

                //then we are going to revert back to the old way
                board = deepCopyBoard(backupBoard);
                teamTurn = initialTeamTurn;
            }

        }
        //this either returns an empty set or a set that has been populated
        return validMoves;
    }

    private ChessBoard deepCopyBoard(ChessBoard board){
        //first we create the copied board we will return
        ChessBoard copiedBoard = new ChessBoard();
        //for each space in the board
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                //we are going to look at each square
                ChessPosition square = new ChessPosition(i,j);

                //if the boards spot is not null
                if(board.getPiece(square) != null){
                    //then we will look at the piece and make a new one
                    ChessPiece copiedPiece = new ChessPiece(board.getPiece(square).getTeamColor(),
                            board.getPiece(square).getPieceType());
                    //we will add that piece to the copied board
                    copiedBoard.addPiece(square, copiedPiece);
                }
            }
        }
        return copiedBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean valid = false;
        //first things first we need to get the piece that made the move
        ChessPiece movedPiece = board.getPiece(move.getStartPosition());

        //if its the wrong move than its an invalid move exception
        if(teamTurn != movedPiece.getTeamColor()){
            throw new InvalidMoveException();
        }

        if(validMoves(move.getStartPosition()).contains(move)){
            valid = true;
        }

        //if the move is valid then do the move
        if(valid){
            if(move.getPromotionPiece() != null){
                ChessPiece newPiece = new ChessPiece(movedPiece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), newPiece);
            }
            else{
                board.addPiece(move.getEndPosition(),movedPiece);
            }
            board.addPiece(move.getStartPosition(), null);

            //switch sides
            if(teamTurn == TeamColor.WHITE)
                teamTurn = TeamColor.BLACK;
            else
                teamTurn = TeamColor.WHITE;
        }
        else{
            throw new InvalidMoveException();
        }

    }

    /**
     * This iis a helper function that scans the board for the current kings position
     */
    private ChessPosition findKing(TeamColor teamColor, ChessBoard board){
        OUTER: for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition proposedPosition = new ChessPosition(i,j);
                if(board.getPiece(proposedPosition) != null){
                    if(board.getPiece(proposedPosition).getTeamColor() == teamColor
                            && board.getPiece(proposedPosition).getPieceType() == ChessPiece.PieceType.KING){
                        return proposedPosition;
                    }
                }
            }
        }
        return null;
    }

    /**
     * this is a helper function that scans for all the pieces on the opposite team
     */
    private HashSet<ChessPosition> findEnemyPieces(TeamColor teamColor, ChessBoard board){
        HashSet<ChessPosition> enemyPieces = new HashSet<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                if(board.getPiece(new ChessPosition(i,j)) != null){
                    if(board.getPiece(new ChessPosition(i,j)).getTeamColor() != teamColor){
                        enemyPieces.add(new ChessPosition(i,j));
                    }
                }
            }
        }
        return enemyPieces;
    }

    private HashSet<ChessPosition> findFriendlyPieces(TeamColor teamColor){
        HashSet<ChessPosition> friendlyPieces= new HashSet<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                if(board.getPiece(new ChessPosition(i,j)) != null){
                    if(board.getPiece(new ChessPosition(i,j)).getTeamColor() == teamColor){
                        friendlyPieces.add(new ChessPosition(i,j));
                    }
                }
            }
        }
        return friendlyPieces;
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
        //first we have to check what the
        HashSet<ChessPosition> enemyLocations = findEnemyPieces(teamColor, board);

        if(findKing(teamColor,board) != null) {
            //for each enemy in the enemyLocations
            for(ChessPosition enemyLocation: enemyLocations){
                //set the enemyPiece that one and go through each of its moves
                ChessPiece enemyPiece = board.getPiece(enemyLocation);
                for(ChessMove move : enemyPiece.pieceMoves(board,enemyLocation)){
                    if((move.getEndPosition().getRow() == findKing(teamColor,board).getRow()) &&
                            (move.getEndPosition().getColumn() == findKing(teamColor,board).getColumn())){
                        return true;
                    }
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
        if(isInCheck(teamColor)){
            if(isInStalemate(teamColor)){
                return true;
            }
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
        HashSet<ChessPosition> friendlyLocations = findFriendlyPieces(teamColor);
        for(ChessPosition friendlyLocation: friendlyLocations){
            //if the there is at least one set of moves from the friendly team that is not null then there is no stalemate
            if(!validMoves(friendlyLocation).isEmpty()){
                return false;
            }
        }
        return true;
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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
