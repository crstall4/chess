package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {
    final private HashMap<String, String> authTokens = new HashMap<>();

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS auth (
            authToken varchar(256) NOT NULL,
            username varchar(256) NOT NULL,
            PRIMARY KEY (authToken)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };


    public SQLAuthDAO() throws ResponseException{
        SQLHelper.configureDatabase(createStatements);
    }

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        String token = UUID.randomUUID().toString();

        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        SQLHelper.executeUpdate(statement, token, username);
        return new AuthData(token, username);
    }

    @Override
    public void deleteAuthData(String token) throws ResponseException {
        confirmAuth(token);
        String statement = "DELETE FROM auth WHERE authToken = ?";
        SQLHelper.executeUpdate(statement, token);
    }

    @Override
    public void confirmAuth(String token) throws ResponseException {
        getUsername(token);
    }

    @Override
    public void clear() throws ResponseException {
        String statement = "TRUNCATE TABLE auth";
        SQLHelper.executeUpdate(statement);
    }

    @Override
    public String getUsername(String token) throws ResponseException {
        String statement = "SELECT username FROM auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        throw new ResponseException(401, "Error: Unauthorized. that auth token didnt exist");
    }



}
