package chess;

import java.util.ArrayList;
import java.util.Collection;
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
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        }
        else{
            //returns a null
            return new ArrayList<ChessMove>();
        }
    }

    /**
     * this will be all of the positions the bishop will be able to go
     *
     * We will take the first position and basically move it by adding one to the rows and columns or doing a combination of - and positives
     * @param board
     * @param piecePosition
     * @return bMoves
     */
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition piecePosition){
        //first we create a arrayList of the bishop moves
        ArrayList<ChessMove> bMoves = new ArrayList<ChessMove>();

        //these are the different directions a bishop can go
        int [][] bDirections = {{1,1},{-1,1},{-1,-1},{1,-1}};

        //for each of the directions
        for(int[] directions: bDirections){
            //the first in the directions array is the row change the second is the col change
            int rowChange = directions[0];
            int colChange = directions[1];

            //first we set it to the place the piece position
            ChessPosition possibleMove = piecePosition;

            //while the peice is still on the board
            while((possibleMove.getRow() <  8 && possibleMove.getColumn() < 8) && (possibleMove.getRow() >  1 && possibleMove.getColumn() > 1)){
                //augment the pieces in the current direction change
                possibleMove = new ChessPosition(possibleMove.getRow() + rowChange,possibleMove.getColumn() + colChange);

                //here we are going to check if there is a friendly or an any blocking our way IE the piece is not null
                //first we are going to make piece check to match with the chessBoard
                if(ChessBoard.getPiece(possibleMove) != null) {
                    //if he is friendly(same color)
                    if (this.pieceColor == ChessBoard.getPiece(possibleMove).pieceColor &&
                            ((possibleMove.getRow() != piecePosition.getRow()) && (possibleMove.getColumn() != piecePosition.getColumn()))){
                        //friendly don't add it and go to the next one
                        break;
                    }
                    //if he is non-friendly(different color)
                    else if (this.pieceColor != ChessBoard.getPiece(possibleMove).pieceColor) {
                        //add current position to possible moves because you will capture the piece and then stop
                        bMoves.add(new ChessMove(piecePosition, possibleMove, PieceType.BISHOP));
                        System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
                        break;
                    }
                }

                //add it to the moves
                bMoves.add(new ChessMove(piecePosition,possibleMove,PieceType.BISHOP));
                System.out.println(possibleMove.getRow() + ", " +possibleMove.getColumn());
            }
        }

        return bMoves;
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
