package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition test1 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
        GeneralMoveCalculator.trySpot(moves,test1,board,myPosition);
        ChessPosition test2 = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
        GeneralMoveCalculator.trySpot(moves,test2,board,myPosition);
        ChessPosition test3 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
        GeneralMoveCalculator.trySpot(moves,test3,board,myPosition);
        ChessPosition test4 = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
        GeneralMoveCalculator.trySpot(moves,test4,board,myPosition);
        ChessPosition test5 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
        GeneralMoveCalculator.trySpot(moves,test5,board,myPosition);
        ChessPosition test6 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
        GeneralMoveCalculator.trySpot(moves,test6,board,myPosition);
        ChessPosition test7 = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
        GeneralMoveCalculator.trySpot(moves,test7,board,myPosition);
        ChessPosition test8 = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
        GeneralMoveCalculator.trySpot(moves,test8,board,myPosition);


        return moves;
    }
}
