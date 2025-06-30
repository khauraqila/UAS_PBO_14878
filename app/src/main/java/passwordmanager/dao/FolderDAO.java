package passwordmanager.dao;

import passwordmanager.model.Folder;
import passwordmanager.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDAO {

    public static void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS folder (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                user_id INTEGER NOT NULL
            );
        """;

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Folder> getFoldersByUserId(int userId) {
        List<Folder> folders = new ArrayList<>();
        String sql = "SELECT * FROM folder WHERE user_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                folders.add(new Folder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return folders;
    }

    public static boolean addFolder(Folder folder) {
        String sql = "INSERT INTO folder(name, user_id) VALUES (?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, folder.getName());
            stmt.setInt(2, folder.getUserId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Folder getFolderByName(String name, int userId) {
        String sql = "SELECT * FROM folder WHERE name = ? AND user_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Folder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Folder getFolderById(int folderId) {
        String sql = "SELECT * FROM folder WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, folderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Folder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean deleteFolder(int folderId) {
        String sql = "DELETE FROM folder WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, folderId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFolderById(int folderId) {
        return deleteFolder(folderId);
    }
}
