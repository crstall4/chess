package client;

import chess.*;


public class ClientMain {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

       try {
           new ChessClient(serverUrl).run();

       } catch (Throwable ex) {
           System.out.printf("Unable to start server: %s%n", ex.getMessage());
       }

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        ChessGame bruh = new ChessGame();
        System.out.println(bruh.getBoard());

    }
}
