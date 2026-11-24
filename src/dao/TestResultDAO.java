package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.TestResult;

public class TestResultDAO {

    public void insert(int studentId, String testType, int score, int total) throws SQLException {
        String sql = "INSERT INTO test_result (student_id, test_type, score, total, test_date) "
                + "VALUES (?, ?, ?, ?, CURDATE())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, testType);
            ps.setInt(3, score);
            ps.setInt(4, total);
            ps.executeUpdate();
        }
    }

    public List<TestResult> findByStudent(int studentId) throws SQLException {
        List<TestResult> list = new ArrayList<>();
        String sql = "SELECT * FROM test_result WHERE student_id = ? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TestResult(rs.getInt("id"), rs.getInt("student_id"), rs.getString("test_type"),
                            rs.getInt("score"), rs.getInt("total"), rs.getString("test_date")));
                }
            }
        }
        return list;
    }
}
