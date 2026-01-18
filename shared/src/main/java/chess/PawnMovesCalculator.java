package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{

    private void addMoves(Collection<ChessMove> moves, ChessPosition test, ChessBoard board, ChessPosition myPosition){
        if((board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE && test.getRow()==8) || (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK && test.getRow()==1)){
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), ChessPiece.PieceType.BISHOP));
        }
        else {
            moves.add(new ChessMove(myPosition, new ChessPosition(test.getRow(), test.getColumn()), null));
        }
    }

    private void trySpot(Collection<ChessMove> moves, ChessPosition test, ChessBoard board, ChessPosition myPosition){
        if(!(test.getRow() <= 8 && test.getColumn() <= 8 && test.getRow() > 0 && test.getColumn() > 0)){
            return;
        }
        if(board.getPiece(test) == null){
            addMoves(moves,test,board,myPosition);
        }
    }

    private void tryCapture(Collection<ChessMove> moves, ChessPosition test, ChessBoard board, ChessPosition myPosition){
        if(!(test.getRow() <= 8 && test.getColumn() <= 8 && test.getRow() > 0 && test.getColumn() > 0)){
            return;
        }
        if (board.getPiece(test) != null && (board.getPiece(test).getTeamColor() != board.getPiece(myPosition).getTeamColor())) {
            addMoves(moves,test,board,myPosition);
        }
    }
    private void initialMove(int forwards, Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition){
        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow()!=2){
            return;
        }
        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow()!=7) {
            return;
        }
        ChessPosition test1 = new ChessPosition(myPosition.getRow()+forwards+forwards, myPosition.getColumn());
        ChessPosition test2 = new ChessPosition(myPosition.getRow()+forwards, myPosition.getColumn());

        if(!(test1.getRow() <= 8 && test1.getColumn() <= 8 && test1.getRow() > 0 && test1.getColumn() > 0)){
            return;
        }
        if(board.getPiece(test2) == null && board.getPiece(test1) == null){
            moves.add(new ChessMove(myPosition, new ChessPosition(test1.getRow(), test1.getColumn()), null));
        }

    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int forwards;
        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            forwards = 1;
        }
        else{
            forwards = -1;
        }
        ChessPosition test = new ChessPosition(myPosition.getRow()+forwards, myPosition.getColumn());
        trySpot(moves,test,board,myPosition);
        ChessPosition test2 = new ChessPosition(myPosition.getRow()+forwards, myPosition.getColumn()+1);
        tryCapture(moves,test2,board,myPosition);
        ChessPosition test3 = new ChessPosition(myPosition.getRow()+forwards, myPosition.getColumn()-1);
        tryCapture(moves,test3,board,myPosition);
        initialMove(forwards,moves,board,myPosition);


        return moves;
    }
}
