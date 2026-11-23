package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Internship;
import model.Job;

public class OpportunityDAO {

    private static final String BASE_JOB =
            "SELECT j.id, j.company_id, c.name AS company_name, c.verified, j.role, j.location, "
          + "j.lpa, j.min_cgpa, j.required_skills, j.job_type, j.probation_months "
          + "FROM job j JOIN company c ON j.company_id = c.id ";

    private Job mapJob(ResultSet rs) throws SQLException {
        Job job = new Job(rs.getInt("id"), rs.getInt("company_id"), rs.getString("company_name"),
                rs.getString("role"), rs.getString("required_skills"), rs.getDouble("min_cgpa"),
                rs.getString("location"), rs.getDouble("lpa"), rs.getString("job_type"),
                rs.getInt("probation_months"));
        job.setCompanyVerified(rs.getInt("verified") == 1);
        return job;
    }

    public void insertJob(Job j) throws SQLException {
        String sql = "INSERT INTO job (company_id, role, location, lpa, min_cgpa, required_skills, "
                + "job_type, probation_months) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, j.getCompanyId());
            ps.setString(2, j.getRole());
            ps.setString(3, j.getLocation());
            ps.setDouble(4, j.getLpa());
            ps.setDouble(5, j.getMinCgpa());
            ps.setString(6, j.getRequiredSkills());
            ps.setString(7, j.getJobType());
            ps.setInt(8, j.getProbationMonths());
            ps.executeUpdate();
        }
    }

    public List<Job> findAllVerifiedJobs() throws SQLException {
        return queryJobs(BASE_JOB + "WHERE c.verified = 1 ORDER BY j.lpa DESC", 0);
    }

    public List<Job> findJobsByCompany(int companyId) throws SQLException {
        return queryJobs(BASE_JOB + "WHERE j.company_id = ? ORDER BY j.id", companyId);
    }

    public Job findJobById(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        List<Job> list = queryJobs(BASE_JOB + "WHERE j.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Job> queryJobs(String sql, int param) throws SQLException {
        List<Job> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param > 0) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJob(rs));
                }
            }
        }
        return list;
    }

    public int updateJob(Job j) throws SQLException {
        String sql = "UPDATE job SET role = ?, location = ?, lpa = ?, min_cgpa = ?, required_skills = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, j.getRole());
            ps.setString(2, j.getLocation());
            ps.setDouble(3, j.getLpa());
            ps.setDouble(4, j.getMinCgpa());
            ps.setString(5, j.getRequiredSkills());
            ps.setInt(6, j.getId());
            return ps.executeUpdate();
        }
    }

    public int deleteJob(int jobId, int companyId) throws SQLException {
        String sql = "DELETE FROM job WHERE id = ? AND company_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, companyId);
            return ps.executeUpdate();
        }
    }

    private static final String BASE_INTERNSHIP =
            "SELECT i.id, i.company_id, c.name AS company_name, c.verified, i.role, i.min_cgpa, "
          + "i.required_skills, i.paid, i.stipend, i.duration_months "
          + "FROM internship i JOIN company c ON i.company_id = c.id ";

    private Internship mapInternship(ResultSet rs) throws SQLException {
        Internship in = new Internship(rs.getInt("id"), rs.getInt("company_id"), rs.getString("company_name"),
                rs.getString("role"), rs.getString("required_skills"), rs.getDouble("min_cgpa"),
                rs.getInt("paid") == 1, rs.getDouble("stipend"), rs.getInt("duration_months"));
        in.setCompanyVerified(rs.getInt("verified") == 1);
        return in;
    }

    public void insertInternship(Internship in) throws SQLException {
        String sql = "INSERT INTO internship (company_id, role, min_cgpa, required_skills, paid, stipend, "
                + "duration_months) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, in.getCompanyId());
            ps.setString(2, in.getRole());
            ps.setDouble(3, in.getMinCgpa());
            ps.setString(4, in.getRequiredSkills());
            ps.setInt(5, in.isPaid() ? 1 : 0);
            ps.setDouble(6, in.getStipend());
            ps.setInt(7, in.getDurationMonths());
            ps.executeUpdate();
        }
    }

    public List<Internship> findAllVerifiedInternships() throws SQLException {
        return queryInternships(BASE_INTERNSHIP + "WHERE c.verified = 1 ORDER BY i.id", 0);
    }

    public List<Internship> findInternshipsByCompany(int companyId) throws SQLException {
        return queryInternships(BASE_INTERNSHIP + "WHERE i.company_id = ? ORDER BY i.id", companyId);
    }

    public Internship findInternshipById(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        List<Internship> list = queryInternships(BASE_INTERNSHIP + "WHERE i.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Internship> queryInternships(String sql, int param) throws SQLException {
        List<Internship> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param > 0) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapInternship(rs));
                }
            }
        }
        return list;
    }

    public int deleteInternship(int internshipId, int companyId) throws SQLException {
        String sql = "DELETE FROM internship WHERE id = ? AND company_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, internshipId);
            ps.setInt(2, companyId);
            return ps.executeUpdate();
        }
    }
}
