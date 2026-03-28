package client;

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import exception.ResponseException;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class ChessClient {

    boolean loggedIn;
    private String authToken;
    private final ServerFacade server;
    private String user;
    private GameData[] lastGamesList = new GameData[0];
    
    ChessGame fakeBoard = new ChessGame();


    public ChessClient(String serverUrl) {
        loggedIn = false;
        server = new ServerFacade(serverUrl);
        user = "LOGGED_OUT";
    }

    public void run() {
        System.out.println("Welcome to Chess! Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
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
                    default -> help();
                };
            } else {
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
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
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
            printBoard(fakeBoard, ChessGame.TeamColor.WHITE);
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

            if(params[1].toUpperCase().equals("WHITE")){
                printBoard(fakeBoard, ChessGame.TeamColor.WHITE);
            }
            else{
                printBoard(fakeBoard, ChessGame.TeamColor.BLACK);
            }
            return String.format("Joined Game #%s as %s", params[0], params[1].toUpperCase());
        }
        throw new ResponseException(400, "Expected: <game number> <WHITE|BLACK>");
    }

    public void printBoard(ChessGame game, ChessGame.TeamColor color) {
        ChessBoard board = game.getBoard();

        if(color == ChessGame.TeamColor.WHITE){
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
            for(int row = 8; row >= 1; row--){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " ");
                for(int col = 1; col <= 8; col++){
                    boolean light = (row + col) % 2 == 1;
                    if(light){
                        System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    }
                    else{
                        System.out.print(SET_BG_COLOR_DARK_GREY);
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    if(piece != null){
                        System.out.print(getPieceSymbol(piece));
                    }
                    else{
                        System.out.print(EMPTY);
                    }
                }
                System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + RESET_BG_COLOR);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
        }
        else{
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
            for(int row = 1; row <= 8; row++){
                System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " ");
                for(int col = 8; col >= 1; col--){
                    boolean light = (row + col) % 2 == 1;
                    if(light){
                        System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    }
                    else{
                        System.out.print(SET_BG_COLOR_DARK_GREY);
                    }
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    if(piece != null){
                        System.out.print(getPieceSymbol(piece));
                    }
                    else{
                        System.out.print(EMPTY);
                    }
                }
                System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + row + " " + RESET_BG_COLOR);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
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
        else{
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
    }

}
