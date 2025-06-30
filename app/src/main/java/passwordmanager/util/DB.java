package passwordmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String DB_URL = "jdbc:sqlite:storepassword.db";

    public static Connection getConnection() throws SQLException { // getConnection() mengembalikan objek Connection ke storepassword.db
        return DriverManager.getConnection(DB_URL);
    }
}