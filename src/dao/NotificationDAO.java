package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Notification;

public class NotificationDAO {

    public void insert(Notification n) throws SQLException {
        String sql = "INSERT INTO notification (student_id, message, is_read, priority) VALUES (?, ?, 0, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, n.getStudentId());
            ps.setString(2, n.getMessage());
            ps.setInt(3, n.getPriority());
            ps.executeUpdate();
        }
    }

    public List<Notification> findByStudent(int studentId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE student_id = ? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(rs.getInt("id"), rs.getInt("student_id"), rs.getString("message"),
                            rs.getInt("is_read") == 1, rs.getInt("priority"), rs.getString("created_at")));
                }
            }
        }
        return list;
    }

    public int countUnread(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notification WHERE student_id = ? AND is_read = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void markAllRead(int studentId) throws SQLException {
        String sql = "UPDATE notification SET is_read = 1 WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }

    public void deleteAll(int studentId) throws SQLException {
        String sql = "DELETE FROM notification WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }
}
