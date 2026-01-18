package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{

    private void trySpot(Collection<ChessMove> moves, ChessPosition test, ChessBoard board, ChessPosition myPosition){
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



    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition test1 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
        trySpot(moves,test1,board,myPosition);
        ChessPosition test2 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
        trySpot(moves,test2,board,myPosition);
        ChessPosition test3 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
        trySpot(moves,test3,board,myPosition);
        ChessPosition test4 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
        trySpot(moves,test4,board,myPosition);
        ChessPosition test5 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
        trySpot(moves,test5,board,myPosition);
        ChessPosition test6 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
        trySpot(moves,test6,board,myPosition);
        ChessPosition test7 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
        trySpot(moves,test7,board,myPosition);
        ChessPosition test8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        trySpot(moves,test8,board,myPosition);


        return moves;
    }
}
