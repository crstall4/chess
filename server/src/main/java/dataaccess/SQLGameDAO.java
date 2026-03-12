package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws ResponseException{
        configureDatabase();
    }

    @Override
    public void clear() throws ResponseException {
        String statement = "TRUNCATE TABLE games";
        executeUpdate(statement);
    }

    @Override
    public GameData createGame(GameData game) throws ResponseException {
        int fakeid = new Random().nextInt();
        GameData fakeGame = new GameData(fakeid, null, null, game.gameName(), new ChessGame());
        String gameJson = new Gson().toJson(fakeGame);
        String statement = "INSERT INTO user (fakeid, data) VALUES (?, ?)";
        int gameID = executeUpdateWithInt(statement, fakeid, gameJson);
        GameData finalGame = new GameData(gameID, null, null, game.gameName(), fakeGame.game());
        String finalJson = new Gson().toJson(finalGame);
        String updateStatement = "UPDATE games SET data = ? WHERE gameID = ?";
        executeUpdate(updateStatement, finalJson, gameID);
        return finalGame;
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws ResponseException {
        HashMap<Integer, GameData> games = new HashMap<>();
        String statement = "SELECT data FROM games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("data");
                        int thisId = rs.getInt("gameID");
                        games.put(thisId, new Gson().fromJson(json, GameData.class));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Error retrieving users: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public void joinGame(String playerColor, int gameId, String username) throws ResponseException{

    }



    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              gameID int NOT NULL AUTO_INCREMENT,
              data LONGTEXT NOT NULL,
              PRIMARY KEY (gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(499, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(411, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private int executeUpdateWithInt(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(411, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
