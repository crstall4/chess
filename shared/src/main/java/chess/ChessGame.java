package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        if(piece == null){
            throw new InvalidMoveException("You tried to move a piece that doesn't exist.");
        }
        if(!piece.pieceMoves(board,start).contains(move)){
            throw new InvalidMoveException("You tried to make an invalid move.");
        }
        TeamColor color = piece.getTeamColor();
        if(color != teamTurn){
            throw new InvalidMoveException("You tried to make a move out of turn.");
        }
        ChessPiece.PieceType promoteTo = move.getPromotionPiece();
        if(promoteTo == null){
            promoteTo = piece.getPieceType();
        }
        board.addPiece(start,null);
        board.addPiece(move.getEndPosition(),new ChessPiece(color, promoteTo));
        if (teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        }
        else {
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(0,0);
        Collection<ChessPosition> opponentPossibleMoves = new ArrayList<>();
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null){
                    if(piece.getTeamColor() == teamColor){
                        if(piece.getPieceType() == ChessPiece.PieceType.KING){
                            kingPosition = new ChessPosition(row,col);
                        }
                    }
                    if(piece.getTeamColor() != teamColor){
                        for(ChessMove move : piece.pieceMoves(board,pos)){
                            opponentPossibleMoves.add(move.getEndPosition());
                        }
                    }
                }
            }
        }
        return opponentPossibleMoves.contains(kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        Collection<ChessPosition> myPieces = new ArrayList<>();
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null){
                    if(piece.getTeamColor() == teamColor){
                        myPieces.add(pos);
                    }
                }
            }
        }
        // simulate every possible piece move, and see if any of them result in the king no longer being in check.
        // to do this we probably need to write a deep copy method for chessboard.
        for(ChessPosition pos : myPieces){
            ChessPiece piece = board.getPiece(pos);
            for(ChessMove move : piece.pieceMoves(board,pos)){
                ChessBoard backupBoard = ChessBoard.deepcopy(board);
                TeamColor backupTeamTurn = teamTurn;
                try{
                    makeMove(move);
                    if(!isInCheck(teamColor)){
                        return false;
                    }
                } catch (InvalidMoveException e) {
                    throw new RuntimeException(e);
                }
                board = backupBoard;
                teamTurn = backupTeamTurn;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition pos = new ChessPosition(i,j);
                this.board.addPiece(pos,board.getPiece(pos));
            }
        }
    }
    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
