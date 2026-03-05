package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.GeneralMoveCalculator.moveBy;

public class RookMovesCalculator implements PieceMovesCalculator{



    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(moveBy(1,0,board,myPosition));
        moves.addAll(moveBy(-1,0,board,myPosition));
        moves.addAll(moveBy(0,-1,board,myPosition));
        moves.addAll(moveBy(0,1,board,myPosition));

        return moves;
    }
}
