package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        if (board.getPiece(myPosition).type == PieceType.KING) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
            return kkMoves(board, myPosition, directions);
        } else if (board.getPiece(myPosition).type == PieceType.QUEEN) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
            return rbqMoves(board, myPosition, directions);
        } else if (board.getPiece(myPosition).type == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
            return rbqMoves(board, myPosition, directions);
        } else if (board.getPiece(myPosition).type == PieceType.KNIGHT) {
            int[][] directions = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            return kkMoves(board, myPosition, directions);
        } else if (board.getPiece(myPosition).type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            return rbqMoves(board, myPosition, directions);
        } else if (board.getPiece(myPosition).type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        } else {
            return null;
        }
    }

    private Collection<ChessMove> rbqMoves(ChessBoard board, ChessPosition myPosition, int[][] pieceDirections) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] direction : pieceDirections) {
            //initializing important variable
            int y = direction[0];
            int x = direction[1];
            ChessPosition possibleMove = new ChessPosition(myPosition.getRow(), myPosition.getColumn());

            //while the piece is still the board
            while ((possibleMove.getRow() <= 8 && possibleMove.getRow() >= 1) && possibleMove.getColumn() <= 8 && possibleMove.getColumn() >= 1) {
                possibleMove = new ChessPosition(possibleMove.getRow() + y, possibleMove.getColumn() + x);

                //checking to make sure what I am about to do is a legal move if not break
                if (possibleMove.getRow() > 8 || possibleMove.getRow() < 1 || possibleMove.getColumn() > 8 || possibleMove.getColumn() < 1)
                    break;

                //3 possibilities if it is a friendly piece, break if an enemy piece break and take, if null continue
                if (board.getPiece(possibleMove) != null) {
                    if (board.getPiece(possibleMove).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                        break;
                    } else if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, possibleMove, null));
                        break;
                    }
                }
                moves.add(new ChessMove(myPosition, possibleMove, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> kkMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] direction : directions) {
            //initializing important variable
            int y = direction[0];
            int x = direction[1];

            ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + y, myPosition.getColumn() + x);

            //checking to make sure what I am about to do is a legal move if not break
            if (possibleMove.getRow() > 8 || possibleMove.getRow() < 1 || possibleMove.getColumn() > 8 || possibleMove.getColumn() < 1)
                continue;

            //3 possibilities if it is a friendly piece, break if an enemy piece break and take, if null continue
            if (board.getPiece(possibleMove) != null) {
                if (board.getPiece(possibleMove).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    continue;
                } else if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, possibleMove, null));
                    continue;
                }
            }
            moves.add(new ChessMove(myPosition, possibleMove, null));
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] directions = {{1, 0}, {1, -1}, {1, 1}};
        for (int[] direction : directions) {
            //initializing important variable
            int y = direction[0];
            int x = direction[1];

            if(board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)
                y = y * -1;

            ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + y, myPosition.getColumn() + x);

            //checking to make sure what I am about to do is a legal move if not break
            if (possibleMove.getRow() > 8 || possibleMove.getRow() < 1 || possibleMove.getColumn() > 8 || possibleMove.getColumn() < 1)
                continue;

            //first check whether its going straight or diagonally
            if(x == 0){
                //if the possible move is not null meaning a friend or a foe is blocking the way
                if (board.getPiece(possibleMove) != null)
                    continue;
                    //if it is null then check if its the end if not just add it to the list
                else{
                    promotionCheck(myPosition, moves, possibleMove);
                }

                //here we are going to add the case of the initial move IE if white is still in its starting pos at 7 or Black in hers at 2
                if((board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                        (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)){
                    //if this is the case the change in y will be increased by 2
                    possibleMove = new ChessPosition(myPosition.getRow() + 2*y, myPosition.getColumn());
                    //if the possible move is not null meaning a friend or a foe is blocking the way
                    if (board.getPiece(possibleMove) != null)
                        continue;
                        //if it is null then check if its the end if not just add it to the list
                    else{
                        moves.add(new ChessMove(myPosition, possibleMove, null));
                    }
                }
            }
            //if it's going diagonally
            else{
                //if the location is not null
                if (board.getPiece(possibleMove) != null) {
                    //and if the location is occupied by the enemy then take that as a possible move
                    if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        promotionCheck(myPosition, moves, possibleMove);
                    }
                }
            }
        }
        return moves;
    }

    private void promotionCheck(ChessPosition myPosition, HashSet<ChessMove> moves, ChessPosition possibleMove) {
        if(possibleMove.getRow() == 8 || possibleMove.getRow() == 1){
            moves.add(new ChessMove(myPosition, possibleMove, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, possibleMove, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, possibleMove, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, possibleMove, PieceType.KNIGHT));
        }
        else{
            moves.add(new ChessMove(myPosition, possibleMove, null));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }
}

