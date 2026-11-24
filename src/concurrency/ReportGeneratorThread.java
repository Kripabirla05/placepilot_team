package concurrency;

import analytics.PlacementAnalytics;
import database.DBConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/** Builds a placement summary and placed-student detail file on a worker thread. */
public class ReportGeneratorThread extends Thread {
    private final Path outputFile;
    private final PlacementAnalytics analytics;
    private volatile Exception failure;

    public ReportGeneratorThread(Path outputFile) {
        this(outputFile, new PlacementAnalytics());
    }

    public ReportGeneratorThread(Path outputFile, PlacementAnalytics analytics) {
        super("PlacePilot-report-generator");
        if (outputFile == null || analytics == null) throw new IllegalArgumentException("outputFile and analytics are required");
        this.outputFile = outputFile;
        this.analytics = analytics;
    }

    @Override
    public void run() {
        try {
            Path parent = outputFile.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                writer.write("PlacePilot Placement Report");
                writer.newLine();
                writer.write("Generated: " + LocalDateTime.now());
                writer.newLine();
                writer.newLine();
                writer.write(analytics.getStatistics().toString());
                writer.newLine();
                writePlacementDetails(writer);
            }
            System.out.println("Placement report saved to " + outputFile.toAbsolutePath());
        } catch (SQLException | IOException e) {
            failure = e;
            System.err.println("Could not generate placement report: " + e.getMessage());
        }
    }

    public Exception getFailure() { return failure; }

    private void writePlacementDetails(BufferedWriter writer) throws SQLException, IOException {
        String sql = "SELECT s.id AS student_id, s.name AS student_name, s.email, "
                + "COALESCE(j.role, i.role, 'Opportunity unavailable') AS role, "
                + "COALESCE(cj.name, ci.name, 'Company unavailable') AS company, "
                + "p.package_lpa, p.ppo_status "
                + "FROM placement p JOIN student s ON s.id = p.student_id "
                + "LEFT JOIN job j ON j.id = p.job_id LEFT JOIN company cj ON cj.id = j.company_id "
                + "LEFT JOIN internship i ON i.id = p.internship_id LEFT JOIN company ci ON ci.id = i.company_id "
                + "ORDER BY s.name, s.id";
        writer.write("Placed students");
        writer.newLine();
        writer.write("Student ID | Name | Email | Company | Role | Package (LPA) | PPO status");
        writer.newLine();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                writer.write(result.getInt("student_id") + " | " + safe(result.getString("student_name"))
                        + " | " + safe(result.getString("email")) + " | " + safe(result.getString("company"))
                        + " | " + safe(result.getString("role")) + " | " + result.getDouble("package_lpa")
                        + " | " + safe(result.getString("ppo_status")));
                writer.newLine();
            }
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ');
    }
}
