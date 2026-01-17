package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPosition test = new ChessPosition(myRow, myCol);

        test.setCol(myCol+1);
        test.setRow(myRow+1);
        while(test.getRow() <= 8 && test.getColumn() <= 8){
            if(board.getPiece(test) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(),test.getColumn()), ChessPiece.PieceType.BISHOP));
                test.setCol(test.getColumn()+1);
                test.setRow(test.getRow()+1);
            }
            else {
                moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(),test.getColumn()), ChessPiece.PieceType.BISHOP));
                break;
            }

        }

        return moves;
    }
}
