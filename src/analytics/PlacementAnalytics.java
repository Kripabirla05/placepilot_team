package analytics;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Read-only placement metrics queried from the existing PlacePilot schema. */
public class PlacementAnalytics {

    public Statistics getStatistics() throws SQLException {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM student) AS students, "
                + "(SELECT COUNT(*) FROM company) AS companies, "
                + "(SELECT COUNT(*) FROM job) AS jobs, "
                + "(SELECT COUNT(*) FROM internship) AS internships, "
                + "(SELECT COUNT(*) FROM application) AS applications, "
                + "(SELECT COUNT(DISTINCT student_id) FROM application WHERE LOWER(status) = 'selected') AS selected_students, "
                + "(SELECT COALESCE(AVG(package_lpa), 0) FROM placement WHERE package_lpa > 0) AS average_package, "
                + "(SELECT COALESCE(MAX(package_lpa), 0) FROM placement) AS highest_package";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (!result.next()) throw new SQLException("Statistics query returned no result");
            long students = result.getLong("students");
            long selected = result.getLong("selected_students");
            double percentage = students == 0 ? 0.0 : selected * 100.0 / students;
            return new Statistics(students, result.getLong("companies"), result.getLong("jobs"),
                    result.getLong("internships"), result.getLong("applications"), selected,
                    percentage, result.getDouble("average_package"), result.getDouble("highest_package"));
        }
    }

    public void printStatistics() throws SQLException {
        System.out.println(getStatistics());
    }

    public static final class Statistics {
        private final long totalStudents;
        private final long totalCompanies;
        private final long totalJobs;
        private final long totalInternships;
        private final long totalApplications;
        private final long selectedStudents;
        private final double placementPercentage;
        private final double averagePackageLpa;
        private final double highestPackageLpa;

        public Statistics(long totalStudents, long totalCompanies, long totalJobs, long totalInternships,
                          long totalApplications, long selectedStudents, double placementPercentage,
                          double averagePackageLpa, double highestPackageLpa) {
            this.totalStudents = totalStudents;
            this.totalCompanies = totalCompanies;
            this.totalJobs = totalJobs;
            this.totalInternships = totalInternships;
            this.totalApplications = totalApplications;
            this.selectedStudents = selectedStudents;
            this.placementPercentage = placementPercentage;
            this.averagePackageLpa = averagePackageLpa;
            this.highestPackageLpa = highestPackageLpa;
        }

        public long getTotalStudents() { return totalStudents; }
        public long getTotalCompanies() { return totalCompanies; }
        public long getTotalJobs() { return totalJobs; }
        public long getTotalInternships() { return totalInternships; }
        public long getTotalApplications() { return totalApplications; }
        public long getSelectedStudents() { return selectedStudents; }
        public double getPlacementPercentage() { return placementPercentage; }
        public double getAveragePackageLpa() { return averagePackageLpa; }
        public double getHighestPackageLpa() { return highestPackageLpa; }

        @Override
        public String toString() {
            return String.format("%nPlacement Statistics%n"
                            + "Registered students: %d%nCompanies: %d%nJobs: %d%nInternships: %d%n"
                            + "Applications: %d%nSelected students: %d%nPlacement percentage: %.2f%%%n"
                            + "Average package: %.2f LPA%nHighest package: %.2f LPA%n",
                    totalStudents, totalCompanies, totalJobs, totalInternships, totalApplications,
                    selectedStudents, placementPercentage, averagePackageLpa, highestPackageLpa);
        }
    }
}
