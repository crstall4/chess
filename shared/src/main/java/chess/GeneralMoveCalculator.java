package chess;

import java.util.ArrayList;
import java.util.Collection;

public class GeneralMoveCalculator {

    public static Collection<ChessMove> moveBy(int moveRow, int moveCol, ChessBoard board, ChessPosition myPosition){
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

    public static void trySpot(Collection<ChessMove> moves, ChessPosition test, ChessBoard board, ChessPosition myPosition){
        if(!(test.getRow() <= 8 && test.getColumn() <= 8 && test.getRow() > 0 && test.getColumn() > 0)){
            return;
        }
        if(board.getPiece(test) == null){
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), null));
        }
        else{
            if(board.getPiece(test).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), null));
            }
        }
    }
}
