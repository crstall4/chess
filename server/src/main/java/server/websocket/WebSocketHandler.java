package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session, command);
                case MAKE_MOVE -> makeMove(ctx.session, command);
                case LEAVE -> leave(ctx.session, command);
                case RESIGN -> resign(ctx.session, command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        try {
            authDAO.confirmAuth(command.getAuthToken());
            String username = authDAO.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                connections.sendToSession(session, new ErrorMessage("Error: Game not found"));
                return;
            }

            connections.add(gameID, session);
            connections.sendToSession(session, new LoadGameMessage(gameData.game()));

            if (username.equals(gameData.whiteUsername())) { 
                connections.broadcast(gameID, session, new NotificationMessage(username + " joined as white player"));
            }
            else if (username.equals(gameData.blackUsername())) { 
                connections.broadcast(gameID, session, new NotificationMessage(username + " joined as black player"));
            }
            else { 
                connections.broadcast(gameID, session, new NotificationMessage(username + " is observing"));
            }
            
        } catch (ResponseException e) {
            connections.sendToSession(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void makeMove(Session session, UserGameCommand command) throws IOException {
        try {
            authDAO.confirmAuth(command.getAuthToken());
            String username = authDAO.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = gameDAO.getGame(gameID);
            ChessMove move = command.getMove();
            
            //make sure the game exists
            if (gameData == null) {
                connections.sendToSession(session, new ErrorMessage("Error: Game not found"));
                return;
            }

            ChessGame game = gameData.game();

            //make sure the game is not over
            if (game.isGameOver()) {
                connections.sendToSession(session, new ErrorMessage("Error: The game is already over"));
                return;
            }

            //make sure it is the user's turn
            boolean whiteTurnNotWhitePlayer = game.getTeamTurn() == ChessGame.TeamColor.WHITE && !Objects.equals(username, gameData.whiteUsername());
            boolean blackTurnNotBlackPlayer = game.getTeamTurn() == ChessGame.TeamColor.BLACK && !Objects.equals(username, gameData.blackUsername());
            if (whiteTurnNotWhitePlayer || blackTurnNotBlackPlayer) {
                connections.sendToSession(session, new ErrorMessage("Error: It is not your turn"));
                return;
            }

            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            String pieceName = piece.getPieceType().toString().toLowerCase();
            String moveDesc = username + " moved their " + pieceName + " from " + move.getStartPosition() + " to " + move.getEndPosition();

            //make sure the move is legal
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                connections.sendToSession(session, new ErrorMessage("Error: Invalid move"));
                return;
            }

            gameDAO.updateGame(gameData);
            connections.broadcast(gameID, null, new LoadGameMessage(game));
            connections.broadcast(gameID, session, new NotificationMessage(moveDesc));

            ChessGame.TeamColor nextTeam = game.getTeamTurn();
            if (game.isInCheckmate(nextTeam)) {
                String winner;
                if (nextTeam == ChessGame.TeamColor.WHITE) {
                    winner = gameData.blackUsername();
                } else {
                    winner = gameData.whiteUsername();
                }
                game.setGameOver(true);
                gameDAO.updateGame(gameData);
                connections.broadcast(gameID, null, new NotificationMessage("Checkmate! " + winner + " wins!"));
            } else if (game.isInStalemate(nextTeam)) {
                game.setGameOver(true);
                gameDAO.updateGame(gameData);
                connections.broadcast(gameID, null, new NotificationMessage("Stalemate! The game is a draw."));
            } else if (game.isInCheck(nextTeam)) {
                String checkedPlayer;
                if (nextTeam == ChessGame.TeamColor.WHITE) {
                    checkedPlayer = gameData.whiteUsername();
                } else {
                    checkedPlayer = gameData.blackUsername();
                }
                connections.broadcast(gameID, null, new NotificationMessage(checkedPlayer + " is in check."));
            }

        } catch (ResponseException e) {
            connections.sendToSession(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void leave(Session session, UserGameCommand command) throws IOException {
        try {
            authDAO.confirmAuth(command.getAuthToken());
            String username = authDAO.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData != null) {
                GameData updated;
                if (username.equals(gameData.whiteUsername())) {
                    updated = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                    gameDAO.updateGame(updated);
                }
                if (username.equals(gameData.blackUsername())) {
                    updated = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                    gameDAO.updateGame(updated);
                }
            }

            connections.remove(gameID, session);
            connections.broadcast(gameID, null, new NotificationMessage(username + " left the game"));
        } catch (ResponseException e) {
            connections.sendToSession(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void resign(Session session, UserGameCommand command) throws IOException {
        try {
            authDAO.confirmAuth(command.getAuthToken());
            String username = authDAO.getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                connections.sendToSession(session, new ErrorMessage("Error: Game not found"));
                return;
            }

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                connections.sendToSession(session, new ErrorMessage("Error: Observers cannot resign"));
                return;
            }

            if (gameData.game().isGameOver()) {
                connections.sendToSession(session, new ErrorMessage("Error: The game is already over"));
                return;
            }

            gameData.game().setGameOver(true);
            gameDAO.updateGame(gameData);

            connections.broadcast(gameID, null, new NotificationMessage(username + " resigned from the game"));
        } catch (ResponseException e) {
            connections.sendToSession(session, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

}