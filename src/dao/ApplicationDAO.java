package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Application;

public class ApplicationDAO {

    private static final String BASE =
            "SELECT a.id, a.student_id, a.job_id, a.internship_id, a.status, a.applied_date, "
          + "s.name AS student_name, COALESCE(j.role, i.role) AS role, COALESCE(cj.name, ci.name) AS company "
          + "FROM application a JOIN student s ON a.student_id = s.id "
          + "LEFT JOIN job j ON a.job_id = j.id LEFT JOIN company cj ON j.company_id = cj.id "
          + "LEFT JOIN internship i ON a.internship_id = i.id LEFT JOIN company ci ON i.company_id = ci.id ";

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application(rs.getInt("id"), rs.getInt("student_id"), rs.getInt("job_id"),
                rs.getInt("internship_id"), rs.getString("status"), rs.getString("applied_date"));
        a.setStudentName(rs.getString("student_name"));
        a.setRole(rs.getString("role"));
        a.setCompanyName(rs.getString("company"));
        return a;
    }

    public void insert(int studentId, int jobId, int internshipId) throws SQLException {
        String sql = "INSERT INTO application (student_id, job_id, internship_id, status, applied_date) "
                + "VALUES (?, ?, ?, 'Applied', CURDATE())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            if (jobId > 0) { ps.setInt(2, jobId); } else { ps.setNull(2, Types.INTEGER); }
            if (internshipId > 0) { ps.setInt(3, internshipId); } else { ps.setNull(3, Types.INTEGER); }
            ps.executeUpdate();
        }
    }

    public boolean alreadyApplied(int studentId, int jobId, int internshipId) throws SQLException {
        String sql = jobId > 0
                ? "SELECT id FROM application WHERE student_id = ? AND job_id = ?"
                : "SELECT id FROM application WHERE student_id = ? AND internship_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, jobId > 0 ? jobId : internshipId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public void updateStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE application SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            ps.executeUpdate();
        }
    }
}
