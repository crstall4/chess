package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator{

    private Collection<ChessMove> moveBy(int moveRow, int moveCol, ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition test = new ChessPosition(myPosition.getRow()+moveRow, myPosition.getColumn()+moveCol);
        while(test.getRow() <= 8 && test.getColumn() <= 8 && test.getRow() > 0 && test.getColumn() > 0){
            if(board.getPiece(test) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(),test.getColumn()), null));
                test.setRow(test.getRow()+moveRow);
                test.setCol(test.getColumn()+moveCol);
            }
            else {
                if (board.getPiece(test).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), null));
                }
                break;
            }
        }

        return moves;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(moveBy(1,1,board,myPosition));
        moves.addAll(moveBy(-1,-1,board,myPosition));
        moves.addAll(moveBy(1,-1,board,myPosition));
        moves.addAll(moveBy(-1,1,board,myPosition));
        moves.addAll(moveBy(1,0,board,myPosition));
        moves.addAll(moveBy(-1,0,board,myPosition));
        moves.addAll(moveBy(0,-1,board,myPosition));
        moves.addAll(moveBy(0,1,board,myPosition));

        return moves;
    }
}
