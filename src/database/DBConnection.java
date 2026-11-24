package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Allow each developer to use local credentials without committing passwords.
    private static final String URL = settingOrDefault("PLACEPILOT_DB_URL", "jdbc:mysql://localhost:3306/placepilot_db");
    private static final String USER = settingOrDefault("PLACEPILOT_DB_USER", "root");
    private static final String PASSWORD = settingOrDefault("PLACEPILOT_DB_PASSWORD", "root");

    private static String settingOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found. Add mysql-connector-j.jar in classpath (lib folder).");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
            return false;
        }
    }
}
