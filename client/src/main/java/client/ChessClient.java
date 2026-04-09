package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import exception.ResponseException;
import facade.ServerFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import static ui.EscapeSequences.*;

public class ChessClient {

    boolean loggedIn;
    boolean gameJoined;
    private String authToken;
    private final ServerFacade server;
    private final String serverUrl;
    private String user;
    private GameData[] lastGamesList = new GameData[0];
    private WebSocketFacade ws;
    private ChessGame.TeamColor myColor;
    private int currentGameID;

    private ChessGame currentGame;
    private final Scanner scanner = new Scanner(System.in);


    public ChessClient(String serverUrl) {
        loggedIn = false;
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        user = "LOGGED_OUT";
    }

    public void run() {
        System.out.println("Welcome to Chess! Sign in to start.");
        System.out.print(help());

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                if(msg == null || msg.isBlank()){
                    msg = "An unexpected error occurred.";
                }
                else{
                    msg = "Error: " + msg;
                }

                System.out.println(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print(RESET_TEXT_COLOR + "[" + user + "] >>> " + SET_TEXT_COLOR_GREEN);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (!loggedIn) {
                return switch (cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    case "clear" -> clear();
                    default -> help();
                };
            } else {
                if(!gameJoined){
                    return switch (cmd) {
                        case "logout" -> logout();
                        case "create" -> createGame(params);
                        case "list" -> listGames(params);
                        case "join" -> join(params);
                        case "observe" -> observe(params);
                        case "quit" -> "quit";
                        default -> help();
                    };
                }
                else{
                    return switch (cmd) {
                        case "redraw" -> redraw(params);
                        case "leave" -> leave(params);
                        case "move" -> move(params);
                        case "resign" -> resign(params);
                        case "legal-moves" -> legalMoves(params);
                        default -> help();
                    };
                }
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String clear() throws ResponseException {
        server.clear();
        return "Database cleared.";
    }

    public String doesNothing(String[] params) throws ResponseException {
        return "This command does nothing.";
    }


    public String login(String[] params) throws ResponseException {
        if (params.length >= 2) {
            var auth = server.login(params[0], params[1]);
            authToken = auth.authToken();
            loggedIn = true;
            user = auth.username();
            lastGamesList = server.listGames(authToken);
            return String.format("Logged in as %s.", auth.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }


    public String createGame(String[] params) throws ResponseException {
        if (params.length >= 1) {
            server.createGame(params[0], authToken);
            lastGamesList = server.listGames(authToken);
            return String.format("Game '%s' created.", params[0]);
        }
        throw new ResponseException(400, "Expected: <game name>");
    }

    public String listGames(String[] params) throws ResponseException {
        lastGamesList = server.listGames(authToken);
        if (lastGamesList.length == 0) {
            return "No games found.";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < lastGamesList.length; i++) {
            var g = lastGamesList[i];
            String white = g.whiteUsername() != null ? g.whiteUsername() : "OPEN";
            String black = g.blackUsername() != null ? g.blackUsername() : "OPEN";
            output.append("Game #" + (i + 1)).append(": ").append(g.gameName())
                  .append("\n - white: ").append(white)
                  .append("\n - black: ").append(black).append("\n\n");
        }
        return output.toString();
    }

    public String logout() throws ResponseException {
        server.logout(authToken);
        authToken = null;
        loggedIn = false;
        user = "LOGGED_OUT";
        return "Logged out.";
    }

    public String register(String[] params) throws ResponseException {
        if (params.length >= 3) {
            var auth = server.register(params[0], params[1], params[2]);
            authToken = auth.authToken();
            loggedIn = true;
            user = auth.username();
            lastGamesList = server.listGames(authToken);
            return String.format("Registered and logged in as %s.", auth.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String observe(String[] params) throws ResponseException {
        if (params.length >= 1 && params[0].matches("\\d+")) {
            int index = Integer.parseInt(params[0]) - 1;
            if (index < 0 || index >= lastGamesList.length) {
                throw new ResponseException(400, "Invalid game number. Use 'list' to see available games.");
            }
            int gameID = lastGamesList[index].gameID();
            myColor = ChessGame.TeamColor.WHITE;
            currentGameID = gameID;
            connectWebSocket(gameID);
            gameJoined = true;
            return String.format("Observing Game #%s", params[0]);
        }
        throw new ResponseException(400, "Expected: <game number>");
    }

    public String join(String[] params) throws ResponseException {
        lastGamesList = server.listGames(authToken);
        if (params.length >= 2 && params[0].matches("\\d+")) {
            int index = Integer.parseInt(params[0]) - 1;
            if (index < 0 || index >= lastGamesList.length) {
                throw new ResponseException(400, "Invalid game number. Use 'list' to see available games.");
            }
            int gameID = lastGamesList[index].gameID();
            server.join(gameID, params[1], authToken);
            myColor = params[1].toUpperCase().equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            currentGameID = gameID;
            connectWebSocket(gameID);
            gameJoined = true;
            return String.format("Joined Game #%s as %s", params[0], params[1].toUpperCase());
        }
        throw new ResponseException(400, "Expected: <game number> <WHITE|BLACK>");
    }

    public String move(String[] params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Expected: move <FROM> <TO> (example: move e2 e4)");
        }
        ChessMove move = new ChessMove(parsePosition(params[0]), parsePosition(params[1]), null);
        try {
            ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, currentGameID, move));
        } catch (Exception e) {
            throw new ResponseException(500, "Failed to send move: " + e.getMessage());
        }
        return "";
    }

    public String leave(String[] params) throws ResponseException {
        try {
            ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, currentGameID, null));
        } catch (Exception e) {
            throw new ResponseException(500, "Failed to send leave: " + e.getMessage());
        }
        ws = null;
        gameJoined = false;
        return "Left the game.";
    }

    public String resign(String[] params) throws ResponseException {
        System.out.println("Are you sure you want to resign? (yes/no)");
        printPrompt();
        String confirmationMessage = scanner.nextLine();
        if (!confirmationMessage.equalsIgnoreCase("yes")) {
            return "Resign cancelled";
        }


        try {
            ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, currentGameID, null));
        } catch (Exception e) {
            throw new ResponseException(500, "Failed to resign: " + e.getMessage());
        }
        return "Resigned from the game.";
    }

    private ChessPosition parsePosition(String pos) throws ResponseException {
        if (pos.length() != 2) {
            throw new ResponseException(400, pos + " is an invalid position. Use format like e2.");
        }
        int col = pos.charAt(0) - 'a' + 1;
        int row = pos.charAt(1) - '0';
        if (row <= 0 || row >= 9 || col <= 0 || col >= 9) {
            throw new ResponseException(400, "Position out of bounds: " + pos);
        }
        return new ChessPosition(row, col);
    }

    private void connectWebSocket(int gameID) throws ResponseException {
        try {
            ws = new WebSocketFacade(serverUrl, this::onServerMessage);
            ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null));
        } catch (Exception e) {
            throw new ResponseException(500, "WebSocket connection failed: " + e.getMessage());
        }
    }

    private void onServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currentGame = ((LoadGameMessage) message).game;
                printBoard(currentGame, myColor);
                printPrompt();
            }
            case NOTIFICATION -> {
                System.out.println("\n" + SET_TEXT_COLOR_MAGENTA + ((NotificationMessage) message).message + RESET_TEXT_COLOR);
                printPrompt();
            }
            case ERROR -> {
                System.out.println("\n" + SET_TEXT_COLOR_RED + ((ErrorMessage) message).errorMessage + RESET_TEXT_COLOR);
                printPrompt();
            }
        }
    }

    public void printBoard(ChessGame game, ChessGame.TeamColor color) {
        printBoard(game, color, Set.of());
    }

    public void printBoard(ChessGame game, ChessGame.TeamColor color, Collection<ChessPosition> highlighted) {
        ChessBoard board = game.getBoard();

        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println("\n" + SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
            for (int row = 8; row >= 1; row--) {
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " ");
                for (int col = 1; col <= 8; col++) {
                    printSquare(board, row, col, highlighted.contains(new ChessPosition(row, col)));
                }
                System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + RESET_BG_COLOR);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
        } else {
            System.out.println("\n" + SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
            for (int row = 1; row <= 8; row++) {
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " ");
                for (int col = 8; col >= 1; col--) {
                    printSquare(board, row, col, highlighted.contains(new ChessPosition(row, col)));
                }
                System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + RESET_BG_COLOR);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
        }
    }

    public String redraw(String[] params) throws ResponseException {
        if (currentGame == null) {
            throw new ResponseException(400, "No game loaded.");
        }
        printBoard(currentGame, myColor);
        return "";
    }

    public String legalMoves(String[] params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Expected: legal-moves <SQUARE> (example: legal-moves e2)");
        }
        if (currentGame == null) {
            throw new ResponseException(400, "No game loaded.");
        }
        ChessPosition position = parsePosition(params[0]);
        Set<ChessPosition> highlighted = currentGame.validMoves(position).stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toSet());
        highlighted.add(position);
        printBoard(currentGame, myColor, highlighted);
        return "";
    }

    private void printSquare(ChessBoard board, int row, int col, boolean highlight) {
        if (highlight) {
            System.out.print(SET_BG_COLOR_DARK_GREEN);
        } else if ((row + col) % 2 == 1) {
            System.out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            System.out.print(SET_BG_COLOR_DARK_GREY);
        }
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece != null) {
            System.out.print(getPieceSymbol(piece));
        } else {
            System.out.print(EMPTY);
        }
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_KING : WHITE_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_QUEEN : WHITE_QUEEN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_ROOK : WHITE_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_KNIGHT : WHITE_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_PAWN : WHITE_PAWN;
        };
    }

    public String help() {
        if(!loggedIn){
            return SET_TEXT_COLOR_BLUE + """
                - register <USERNAME> <PASSWORD> <EMAIL>    Register an account
                - login <USERNAME> <PASSWORD>               Login to your account
                - quit                                      Exits the program
                - help                                      Displays text informing the user what actions they can take
                """;
        }
        else if (!gameJoined){
            return """
                    create <NAME>               Creates a game
                    list                        Lists all games
                    join <ID> [WHITE|BLACK]     Join a game
                    observe <ID>                Observe a game
                    logout                      Logout of your account
                    quit                        Exits the program
                    help                        Displays text informing the user what actions they can take
                    """;
        }
        else {
            return """
                    redraw                      Redraws the chess board
                    leave                       Leave the game and return to post-login menu
                    move <FROM> <TO>            Make a move (example: move e2 e4)
                    resign                      Resign from the game
                    legal-moves <SQUARE>        Highlight legal moves for a piece (example: legal-moves e2)
                    help                        Displays text informing the user what actions they can take
                    """;
        }
    }

}
