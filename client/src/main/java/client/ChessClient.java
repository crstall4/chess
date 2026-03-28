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


    public ChessClient(String serverUrl) {
        loggedIn = false;
        server = new ServerFacade(serverUrl);
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
        if(!loggedIn){
            System.out.print(RESET_TEXT_COLOR + "[LOGGED_OUT] ");
        }
        System.out.print(">>> " + SET_TEXT_COLOR_GREEN);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "signout" -> signOut();
                case "quit" -> "quit";
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
            return String.format("Logged in as %s.", auth.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }




    public String signIn(){
        loggedIn = true;
        return "Signed in";
    }

    public String signOut() throws ResponseException {
        loggedIn = false;
        return "Signed out";
    }

    public String register(){

        return "";
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
