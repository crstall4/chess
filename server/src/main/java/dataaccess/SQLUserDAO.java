package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;


public class SQLUserDAO implements UserDAO {
    final private HashMap<Integer, UserData> users = new HashMap<>();

    public SQLUserDAO() throws ResponseException{
        configureDatabase();

    }

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        return new UserData(null,null,null);
    }

    @Override
    public void clear() {

    }

    @Override
    public UserData getUserData(UserData logonAttempt) throws ResponseException{
        return new UserData(null,null,null);
    }

    @Override
    public HashMap<Integer, UserData> getUsers() throws ResponseException{
        return new HashMap<Integer, UserData>();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL,
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

}
