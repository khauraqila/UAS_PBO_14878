package passwordmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import passwordmanager.model.PasswordEntry;
import passwordmanager.util.DB;

public class PasswordDAO {

    public static void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS password_entry (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                username TEXT NOT NULL,
                encrypted_password TEXT NOT NULL,
                hash_key TEXT NOT NULL,
                folder_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                category TEXT NOT NULL
            );
        """;

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addPassword(PasswordEntry entry) {
        String sql = "INSERT INTO password_entry (name, username, encrypted_password, hash_key, folder_id, user_id, category) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getUsername());
            stmt.setString(3, entry.getEncryptedPassword());
            stmt.setString(4, entry.getHashKey());
            stmt.setInt(5, entry.getFolderId());
            stmt.setInt(6, entry.getUserId());
            stmt.setString(7, entry.getCategory());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<PasswordEntry> getPasswordsByFolderId(int folderId) {
        List<PasswordEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM password_entry WHERE folder_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                entries.add(mapResultSetToPasswordEntry(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static List<PasswordEntry> searchByKeyword(String keyword, int userId) {
        List<PasswordEntry> results = new ArrayList<>();
        String sql = """
            SELECT * FROM password_entry
            WHERE user_id = ? AND (
                LOWER(name) LIKE ? OR LOWER(username) LIKE ?
            )
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            String pattern = "%" + keyword.toLowerCase() + "%";
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToPasswordEntry(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static boolean updatePasswordEntry(PasswordEntry entry) {
        String sql = """
            UPDATE password_entry
            SET name = ?, username = ?, encrypted_password = ?, hash_key = ?, category = ?
            WHERE id = ?;
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getUsername());
            stmt.setString(3, entry.getEncryptedPassword());
            stmt.setString(4, entry.getHashKey());
            stmt.setString(5, entry.getCategory());
            stmt.setInt(6, entry.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deletePasswordById(int id) {
        String sql = "DELETE FROM password_entry WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static PasswordEntry mapResultSetToPasswordEntry(ResultSet rs) throws SQLException {
        return new PasswordEntry(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("encrypted_password"),
                rs.getString("hash_key"),
                rs.getInt("folder_id"),
                rs.getInt("user_id"),
                rs.getString("category")
        );
    }
}