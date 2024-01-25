package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static java.lang.System.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger, but does take into account whether a piece is blocked
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //here we are going to filter by piece
        if (type == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        }
        else if(type == PieceType.ROOK){
            return rookMoves(board, myPosition);
        }
        else if(type == PieceType.QUEEN){
            //here we just combine the rook and bishop moves into the queen moves
            ArrayList<ChessMove> queenMoves = new ArrayList<ChessMove>();
            queenMoves.addAll(bishopMoves(board,myPosition));
            queenMoves.addAll(rookMoves(board,myPosition));

            return queenMoves;
        }
        else if(type == PieceType.KING){
            return kingMoves(board, myPosition);
        }
        else if(type == PieceType.PAWN){
            return pawnMoves(board, myPosition);
        }
        else if(type == PieceType.KNIGHT){
            return knightMoves(board, myPosition);
        }
        else{
            //returns a null
            return new ArrayList<ChessMove>();
        }
    }

    /**
     * Helper Function Controls how the Rook Moves
     *
     * @param board
     * @param piecePosition
     * @return bMoves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition piecePosition){
        //first we create a arrayList of the bishop moves
        HashSet<ChessMove> bMoves = new HashSet<>();

        //these are the different directions a bishop can go
        int [][] bDirections = {{1,1},{-1,1},{-1,-1},{1,-1}};

        //for each of the directions
        for(int[] directions: bDirections){
            //the first in the directions array is the row change the second is the col change
            int rowChange = directions[0];
            int colChange = directions[1];

            //first we set it to the place the piece position
            ChessPosition possibleMove = piecePosition;

            //while the piece is still on the board
            while((possibleMove.getRow() <=  8 && possibleMove.getColumn() <= 8) && (possibleMove.getRow() >=  1 && possibleMove.getColumn() >= 1)){
                //augment the pieces in the current direction change
                possibleMove = new ChessPosition(possibleMove.getRow() + rowChange,possibleMove.getColumn() + colChange);

                //here is an edge case catcher that makes sure we don't go too far off the board
                int tooFarX = possibleMove.getColumn() - 1;
                int tooFarY = possibleMove.getRow() - 1;

                if((tooFarX == -1 || tooFarY ==-1) || (tooFarX == 8 || tooFarY == 8))
                    break;

                //here we are going to check if there is a friendly or an any blocking our way IE the piece is not null
                if(board.getPiece(possibleMove) != null) {
                    //if he is friendly(same color)
                    if (this.pieceColor == board.getPiece(possibleMove).pieceColor){
                        //friendly don't add it and go to the next one
                        break;
                    }
                    //if he is non-friendly(different color)
                    else if (this.pieceColor != board.getPiece(possibleMove).pieceColor) {
                        //add current position to possible moves because you will capture the piece and then stop
                        bMoves.add(new ChessMove(piecePosition, possibleMove, null));
                        System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
                        break;
                    }
                }

                //add it to the moves
                bMoves.add(new ChessMove(piecePosition,possibleMove, null));
                System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
            }
        }

        return bMoves;
    }

    /**
     * This Helper Function Controls how the Rook Moves
     *
     * @param board
     * @param piecePosition
     * @return rMoves
     */
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition piecePosition){
        //first we create a arrayList of the rook moves
        ArrayList<ChessMove> rMoves = new ArrayList<ChessMove>();

        //these are the different directions a rook can go
        int [][] rDirections = {{0,1},{0,-1},{-1,0},{1,0}};

        //for each of the directions
        for(int[] directions: rDirections){
            //the first in the directions array is the row change the second is the col change
            int rowChange = directions[0];
            int colChange = directions[1];

            //first we set it to the place the piece position
            ChessPosition possibleMove = piecePosition;

            //while the peice is still on the board
            while((possibleMove.getRow() <=  8 && possibleMove.getColumn() <= 8) && (possibleMove.getRow() >=  1 && possibleMove.getColumn() >= 1)){
                //augment the pieces in the current direction change
                possibleMove = new ChessPosition(possibleMove.getRow() + rowChange,possibleMove.getColumn() + colChange);

                //here is an edge case catcher that makes sure we don't go too far off the board
                int tooFarX = possibleMove.getColumn() - 1;
                int tooFarY = possibleMove.getRow() - 1;

                if((tooFarX == -1 || tooFarY ==-1) || (tooFarX == 8 || tooFarY == 8))
                    break;


                //here we are going to check if there is a friendly or an any blocking our way IE the piece is not null
                if(board.getPiece(possibleMove) != null) {
                    //if he is friendly(same color)
                    if (this.pieceColor == board.getPiece(possibleMove).pieceColor){
                        //friendly don't add it and go to the next one
                        break;
                    }
                    //if he is non-friendly(different color)
                    else if (this.pieceColor != board.getPiece(possibleMove).pieceColor) {
                        //add current position to possible moves because you will capture the piece and then stop
                        rMoves.add(new ChessMove(piecePosition, possibleMove, null));
                        System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
                        break;
                    }
                }

                //add it to the moves
                rMoves.add(new ChessMove(piecePosition,possibleMove, null));
                System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
            }
        }

        return rMoves;
    }

    /**
     * Returns the kings moves
     *
     * Simplified version of Rook and Bishop Functions
     * @param board
     * @param piecePosition
     * @return kMoves
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition piecePosition){
        //first we create a arrayList of the king moves
        ArrayList<ChessMove> kMoves = new ArrayList<ChessMove>();

        //these are the different directions a king can go
        int [][] kDirections = {{0,1},{0,-1},{-1,0},{1,0},{1,1},{-1,1},{-1,-1},{1,-1}};

        //for each of the directions
        for(int[] directions: kDirections){
            //the first in the directions array is the row change the second is the col change
            int rowChange = directions[0];
            int colChange = directions[1];

            //first we set it to the place the piece position
            ChessPosition possibleMove = piecePosition;

            possibleMove = new ChessPosition(possibleMove.getRow() + rowChange,possibleMove.getColumn() + colChange);

            //here is an edge case catcher that makes sure we don't go too far off the board
            int tooFarX = possibleMove.getColumn() - 1;
            int tooFarY = possibleMove.getRow() - 1;

            if((tooFarX == -1 || tooFarY ==-1) || (tooFarX == 8 || tooFarY == 8))
                continue;

            //here we are going to check if there is a friendly or an any blocking our way IE the piece is not null
            if(board.getPiece(possibleMove) != null) {
                //if he is friendly(same color)
                if (this.pieceColor == board.getPiece(possibleMove).pieceColor){
                    //friendly don't add it and go to the next one
                    continue;
                }
                //if he is non-friendly(different color)
                else if (this.pieceColor != board.getPiece(possibleMove).pieceColor) {
                    //add current position to possible moves because you will capture the piece and then stop
                    kMoves.add(new ChessMove(piecePosition, possibleMove, null));
                    System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
                    continue;
                }
            }
            //add it to the moves
            kMoves.add(new ChessMove(piecePosition,possibleMove,null));
            System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
        }
        return kMoves;
    }


    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition piecePosition){
        ArrayList<ChessMove> pMoves = new ArrayList<ChessMove>();

        int[][] pDirections = {{1,0},{1,-1},{1,1}};
        for(int[] directions: pDirections){
            //first we assign the values
            int y = directions[0];
            int x = directions [1];
            //then depending on whether the pawn is black we will modify the y value by making it go down instead of up
            if(board.getPiece(piecePosition).pieceColor == ChessGame.TeamColor.BLACK)
                y = y * -1;

            //if there is no change on the x(ie moving forward) and the row is within at least 1 away from the edges
            if(x == 0 && (piecePosition.getRow() > 1 && piecePosition.getRow() < 8)){
                //then we check for if the space is null if it is then go straight and add it to the collection
                ChessPosition straight = new ChessPosition(piecePosition.getRow() + y, piecePosition.getColumn());

                if(board.getPiece(straight) == null) {
                    if((straight.getRow() == 1 || straight.getRow() == 8)){
                        pMoves.add(new ChessMove(piecePosition, straight, PieceType.BISHOP));
                        pMoves.add(new ChessMove(piecePosition, straight, PieceType.ROOK));
                        pMoves.add(new ChessMove(piecePosition, straight, PieceType.KNIGHT));
                        pMoves.add(new ChessMove(piecePosition, straight, PieceType.QUEEN));
                    }
                    else{
                        pMoves.add(new ChessMove(piecePosition, straight, null));
                        System.out.println(straight.getRow() + ", " + straight.getColumn());
                    }

                    //if the pawn is in its starting position then we are also going to add another possibility of moving 1 more
                    if((board.getPiece(piecePosition).pieceColor == ChessGame.TeamColor.BLACK  && piecePosition.getRow() == 7) ||
                            (board.getPiece(piecePosition).pieceColor == ChessGame.TeamColor.WHITE && piecePosition.getRow() == 2)) {
                        ChessPosition initialStraight = new ChessPosition(piecePosition.getRow() + 2*y, piecePosition.getColumn());

                        //if that position is not already occupied then go ahead
                        if(board.getPiece(initialStraight) == null){
                            pMoves.add(new ChessMove(piecePosition, initialStraight, null));
                            System.out.println(initialStraight.getRow() + ", " + initialStraight.getColumn());
                        }
                    }
                }
            }
            //here we are going to check the diagonal cases
            else {
                //we have to check first if the piece is within the board
                if((piecePosition.getRow() > 1 && piecePosition.getRow() < 8)) {
                    //then check if there is an enemyPiece on the piece you want to go on
                    ChessPosition diagonal = new ChessPosition(piecePosition.getRow() + y, piecePosition.getColumn() + x);
                    //making sure its not just a blank space and its of a different color
                    if(board.getPiece(diagonal) != null && board.getPiece(diagonal).pieceColor != board.getPiece(piecePosition).pieceColor){
                        if((diagonal.getRow() == 1 || diagonal.getRow() == 8)){
                            pMoves.add(new ChessMove(piecePosition, diagonal, PieceType.BISHOP));
                            pMoves.add(new ChessMove(piecePosition, diagonal, PieceType.ROOK));
                            pMoves.add(new ChessMove(piecePosition, diagonal, PieceType.KNIGHT));
                            pMoves.add(new ChessMove(piecePosition, diagonal, PieceType.QUEEN));
                        }
                        else{
                            pMoves.add(new ChessMove(piecePosition, diagonal, null));
                            System.out.println(diagonal.getRow() + ", " + diagonal.getColumn());
                        }
                    }
                }
            }
        }

        return pMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition piecePosition) {
        ArrayList<ChessMove> knMoves = new ArrayList<ChessMove>();

        int[][] pDirections = {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}};
        for (int[] directions : pDirections) {
            //first we assign the values
            int y = directions[0];
            int x = directions[1];

            //then we are going to check if the ending position is out of bounds
            if((((piecePosition.getRow() + y) < 1) || ((piecePosition.getRow() + y) > 8)) ||
                    (((piecePosition.getColumn() + x) < 1) || ((piecePosition.getColumn() + x) > 8)))
                continue;
            //then as long as either the square is null or has an enemy piece we can move there
            ChessPosition possibleMove = new ChessPosition(piecePosition.getRow() + y, piecePosition.getColumn() + x);
            if(board.getPiece(possibleMove) == null ||
                    board.getPiece(possibleMove).pieceColor != board.getPiece(piecePosition).pieceColor){
                knMoves.add(new ChessMove(piecePosition, possibleMove, null));
                System.out.println(possibleMove.getRow() + ", " + possibleMove.getColumn());
            }
        }
        return knMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
