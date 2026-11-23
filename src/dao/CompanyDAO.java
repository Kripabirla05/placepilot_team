package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Company;
import model.Recruiter;

public class CompanyDAO {

    private Company mapCompany(ResultSet rs) throws SQLException {
        return new Company(rs.getInt("id"), rs.getString("name"),
                rs.getString("location"), rs.getInt("verified") == 1);
    }

    public int insertCompany(String name, String location) throws SQLException {
        String sql = "INSERT INTO company (name, location, verified) VALUES (?, ?, 0)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public void insertRecruiter(String name, String email, String password, int companyId) throws SQLException {
        String sql = "INSERT INTO recruiter (name, email, password, company_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setInt(4, companyId);
            ps.executeUpdate();
        }
    }

    public boolean companyNameExists(String name) throws SQLException {
        return exists("SELECT id FROM company WHERE name = ?", name);
    }

    public boolean recruiterEmailExists(String email) throws SQLException {
        return exists("SELECT id FROM recruiter WHERE email = ?", email);
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Recruiter findRecruiter(String email, String password) throws SQLException {
        String sql = "SELECT * FROM recruiter WHERE email = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Recruiter(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                            rs.getString("password"), rs.getInt("company_id"));
                }
            }
        }
        return null;
    }

    public Company findById(int id) throws SQLException {
        String sql = "SELECT * FROM company WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCompany(rs);
                }
            }
        }
        return null;
    }

    public List<Company> findAll() throws SQLException {
        return query("SELECT * FROM company ORDER BY id");
    }

    public List<Company> findUnverified() throws SQLException {
        return query("SELECT * FROM company WHERE verified = 0 ORDER BY id");
    }

    private List<Company> query(String sql) throws SQLException {
        List<Company> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCompany(rs));
            }
        }
        return list;
    }

    public int verify(int companyId) throws SQLException {
        String sql = "UPDATE company SET verified = 1 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            return ps.executeUpdate();
        }
    }

    public void deleteCompany(int companyId) throws SQLException {
        String sql = "DELETE FROM company WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.executeUpdate();
        }
    }
}
