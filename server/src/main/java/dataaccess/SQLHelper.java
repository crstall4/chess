package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.sql.*;

public class SQLHelper {

    public static void configureDatabase(String[] createStatements) throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    public static void executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p); }
                    else if (param instanceof UserData p) { ps.setString(i + 1, p.toString()); }
                    else if (param == null) { ps.setNull(i + 1, Types.NULL); }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public static int executeUpdateWithInt(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p); }
                    else if (param instanceof UserData p) { ps.setString(i + 1, p.toString()); }
                    else if (param == null) { ps.setNull(i + 1, Types.NULL); }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
