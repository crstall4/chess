package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    final private HashMap<String, String> authTokens = new HashMap<>();

    public SQLAuthDAO() throws ResponseException{
        configureDatabase();

    }

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        return new AuthData(null,null);
    }

    @Override
    public void deleteAuthData(String token) throws ResponseException{

    }

    @Override
    public void confirmAuth(String token) throws ResponseException{

    }

    @Override
    public void clear() throws ResponseException {

    }

    @Override
    public String getUsername(String token) throws ResponseException {
        return "hi";
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
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
            throw new ResponseException(411, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
