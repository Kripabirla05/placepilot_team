package main;

import analytics.PlacementAnalytics;
import concurrency.ReportGeneratorThread;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.util.Scanner;

/** Terminal admin login and dashboard for the existing student/company tables. */
public class AdminMenu {
    private final Scanner input;
    private final PlacementAnalytics analytics;

    public AdminMenu() {
        this(new Scanner(System.in), new PlacementAnalytics());
    }

    public AdminMenu(Scanner input, PlacementAnalytics analytics) {
        if (input == null || analytics == null) throw new IllegalArgumentException("input and analytics are required");
        this.input = input;
        this.analytics = analytics;
    }

    /** Standalone entry point; compile the project from its root so packages resolve. */
    public static void main(String[] args) {
        new AdminMenu().run();
    }

    /** Call from the application's main menu when the administrator option is selected. */
    public void run() {
        try {
            if (!login()) return;
            boolean running = true;
            while (running) {
                printMenu();
                int choice = readInt("Choose an option: ", 0, 7);
                try {
                    switch (choice) {
                        case 1: listStudents(); break;
                        case 2: listCompanies(); break;
                        case 3: addCompany(); break;
                        case 4: updateCompanyVerification(); break;
                        case 5: deleteStudent(); break;
                        case 6: analytics.printStatistics(); break;
                        case 7: generateReport(); break;
                        case 0: running = false; break;
                        default: System.out.println("Choose a listed option.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database operation failed: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to connect to the database: " + e.getMessage());
        }
    }

    private boolean login() throws SQLException {
        System.out.println("=== PlacePilot Admin Login ===");
        String username = readRequired("Username: ");
        String password = readRequired("Password: ");
        String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    System.out.println("Login successful.");
                    return true;
                }
            }
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    private void printMenu() {
        System.out.println("\n=== Admin Dashboard ===");
        System.out.println("1. View students");
        System.out.println("2. View companies");
        System.out.println("3. Add company");
        System.out.println("4. Verify/unverify company");
        System.out.println("5. Delete student");
        System.out.println("6. View placement statistics");
        System.out.println("7. Generate placement report");
        System.out.println("0. Exit admin dashboard");
    }

    private void listStudents() throws SQLException {
        String sql = "SELECT id, name, email, branch, cgpa, backlogs FROM student ORDER BY id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            System.out.println("\nID | Name | Email | Branch | CGPA | Backlogs");
            while (result.next()) {
                System.out.printf("%d | %s | %s | %s | %.2f | %d%n", result.getInt("id"),
                        result.getString("name"), result.getString("email"), result.getString("branch"),
                        result.getDouble("cgpa"), result.getInt("backlogs"));
            }
        }
    }

    private void listCompanies() throws SQLException {
        String sql = "SELECT id, name, location, verified FROM company ORDER BY id";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            System.out.println("\nID | Name | Location | Verified");
            while (result.next()) {
                System.out.printf("%d | %s | %s | %s%n", result.getInt("id"), result.getString("name"),
                        result.getString("location"), result.getBoolean("verified") ? "Yes" : "No");
            }
        }
    }

    private void addCompany() throws SQLException {
        String name = readRequired("Company name: ");
        String location = readRequired("Location: ");
        String sql = "INSERT INTO company (name, location) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, location);
            statement.executeUpdate();
            System.out.println("Company added.");
        }
    }

    private void updateCompanyVerification() throws SQLException {
        int id = readInt("Company ID: ", 1, Integer.MAX_VALUE);
        String sql = "UPDATE company SET verified = CASE WHEN verified = 1 THEN 0 ELSE 1 END WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) System.out.println("No company found with that ID.");
            else System.out.println("Company verification status updated.");
        }
    }

    private void deleteStudent() throws SQLException {
        int id = readInt("Student ID to delete: ", 1, Integer.MAX_VALUE);
        System.out.print("Delete this student and cascade their related records? Type YES to confirm: ");
        String confirmation = input.nextLine().trim();
        if (!"YES".equals(confirmation)) {
            System.out.println("Deletion cancelled.");
            return;
        }
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM student WHERE id = ?")) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) System.out.println("No student found with that ID.");
            else System.out.println("Student and dependent records deleted.");
        }
    }

    private void generateReport() {
        String path = readRequired("Report file path: ");
        ReportGeneratorThread report = new ReportGeneratorThread(Paths.get(path));
        report.start();
        System.out.println("Report generation started in a background thread.");
    }

    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }
}
