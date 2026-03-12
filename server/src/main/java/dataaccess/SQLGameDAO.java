package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws ResponseException{
        configureDatabase();

    }

    @Override
    public void clear() {

    }

    @Override
    public GameData createGame(GameData game) throws ResponseException {
        return new GameData(0,null,null,null,null);
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws ResponseException{
        return new HashMap <Integer, GameData>();
    }

    @Override
    public void joinGame(String playerColor, int gameId, String username) throws ResponseException{

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              gameID int NOT NULL AUTO_INCREMENT,
              whiteUsername varchar(256) DEFAULT NULL,
              blackUsername varchar(256) DEFAULT NULL,
              gameName varchar(256) NOT NULL,
              game LONGTEXT NOT NULL,
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

}
