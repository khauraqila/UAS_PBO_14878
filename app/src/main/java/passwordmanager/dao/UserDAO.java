package passwordmanager.dao;

import passwordmanager.model.User;
import passwordmanager.util.DB;

import java.sql.*;

public class UserDAO {

    public static void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS userdata (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                fullname TEXT NOT NULL
            );
        """;

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(User user) {
        String sql = "INSERT INTO userdata(username, password_hash, fullname) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getFullname());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static User findByUsername(String username) {
        String sql = "SELECT * FROM userdata WHERE username = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("fullname")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateUser(User user, String newHashedPassword) {
        String sql = """
            UPDATE userdata
            SET fullname = ?, password_hash = ?
            WHERE id = ?;
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullname());
            stmt.setString(2, newHashedPassword);
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}