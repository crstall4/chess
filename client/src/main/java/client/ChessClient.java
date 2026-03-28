package client;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class ChessClient {

    boolean loggedIn;
    private String authToken;
    private final ServerFacade server;
    private String user;


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
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "quit" -> "quit";
                case "list" -> listGames(params);
                default -> help();
            };
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
            return String.format("Logged in as %s.", auth.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }


    public String createGame(String[] params) throws ResponseException {
        if (params.length >= 1) {
            server.createGame(params[0], authToken);
            return String.format("Game '%s' created.", params[0]);
        }
        throw new ResponseException(400, "Expected: <game name>");
    }

    public String listGames(String[] params) throws ResponseException {
        var games = server.listGames(authToken);
        if(games.length == 0){
            return "No games found.";
        }
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < games.length; i++) {
            var g = games[i];
            String white = g.whiteUsername() != null ? g.whiteUsername() : "OPEN";
            String black = g.blackUsername() != null ? g.blackUsername() : "OPEN";
            output.append("Game #").append(i + 1).append(": ").append(g.gameName()).append("\n - white: ").append(white).append("\n - black: ").append(black).append("\n\n");
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
            return String.format("Registered and logged in as %s.", auth.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
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
