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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              username varchar(256) NOT NULL,
              data LONGTEXT NOT NULL,
              PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLUserDAO() throws ResponseException{
        SQLHelper.configureDatabase(createStatements);
    }

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData newUser = new UserData(user.username(), hashedPassword, user.email());
        String userData = new Gson().toJson(newUser);
        String statement = "INSERT INTO users (username, data) VALUES (?, ?)";
        SQLHelper.executeUpdate(statement, user.username(), userData);
        return newUser;
    }

    @Override
    public void clear() throws ResponseException {
        String statement = "TRUNCATE TABLE users";
        SQLHelper.executeUpdate(statement);
    }

    @Override
    public UserData getUserData(String username) throws ResponseException {
        String statement = "SELECT data FROM users WHERE username = ?";
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
            throw new ResponseException(500, String.format("Error: retrieving user: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public HashMap<Integer, UserData> getUsers() throws ResponseException{
            HashMap<Integer, UserData> users = new HashMap<>();
            String statement = "SELECT data FROM users";
            try (Connection conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement)) {
                    try (var rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String json = rs.getString("data");
                            UserData tempUser = new Gson().fromJson(json, UserData.class);
                            users.put(tempUser.username().hashCode(), tempUser);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new ResponseException(500, String.format("Error: retrieving users: %s", e.getMessage()));
            }
            return users;
        }




}
