package dataaccess;

import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws ResponseException{
        configureDatabase();
    }

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData newUser = new UserData(user.username(), hashedPassword, user.email());
        String userData = new Gson().toJson(newUser);
        String statement = "INSERT INTO user (username, data) VALUES (?, ?)";
        executeUpdate(statement, user.username(), userData);
        return newUser;
    }

    @Override
    public void clear() throws ResponseException {
        String statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
    }

    @Override
    public UserData getUserData(String username) throws ResponseException {
        String statement = "SELECT data FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = rs.getString("data");
                        return new Gson().fromJson(json, UserData.class);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Error retrieving user: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public HashMap<Integer, UserData> getUsers() throws ResponseException{
            HashMap<Integer, UserData> users = new HashMap<>();
            String statement = "SELECT data FROM user";
            try (Connection conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement)) {
                    try (var rs = ps.executeQuery()) {
                        int index = 1;
                        while (rs.next()) {
                            String json = rs.getString("data");
                            users.put(index++, new Gson().fromJson(json, UserData.class));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new ResponseException(500, String.format("Error retrieving users: %s", e.getMessage()));
            }
            return users;
        }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username varchar(256) NOT NULL,
              data LONGTEXT NOT NULL,
              PRIMARY KEY (username)
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
}
