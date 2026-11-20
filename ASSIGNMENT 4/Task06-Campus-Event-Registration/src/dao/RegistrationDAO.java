package task06.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import task06.model.Registration;
import task06.model.Student;

/**
 * Data Access Object for Registrations.
 */
public class RegistrationDAO {

    /**
     * Checks whether a student with the given roll number is already registered for an event.
     */
    public boolean isStudentRegistered(String rollNo, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations r " +
                     "JOIN students s ON r.student_id = s.id " +
                     "WHERE s.roll_no = ? AND r.event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            ps.setInt(2, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Finds or creates a student, then creates an event registration.
     */
    public int register(Student student, int eventId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
            try {
                // 1. Check if student exists
                int studentId = 0;
                String findStudentSql = "SELECT id FROM students WHERE roll_no = ?";
                try (PreparedStatement psFind = conn.prepareStatement(findStudentSql)) {
                    psFind.setString(1, student.getRollNo().trim());
                    try (ResultSet rs = psFind.executeQuery()) {
                        if (rs.next()) {
                            studentId = rs.getInt("id");
                        }
                    }
                }

                // If not, insert student
                if (studentId == 0) {
                    String insertStudentSql = "INSERT INTO students (roll_no, name, email, department, academic_year) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertStudentSql, Statement.RETURN_GENERATED_KEYS)) {
                        psIns.setString(1, student.getRollNo().trim());
                        psIns.setString(2, student.getName().trim());
                        psIns.setString(3, student.getEmail().trim());
                        psIns.setString(4, student.getDepartment().trim());
                        psIns.setInt(5, student.getAcademicYear());
                        psIns.executeUpdate();
                        try (ResultSet rsKeys = psIns.getGeneratedKeys()) {
                            if (rsKeys.next()) {
                                studentId = rsKeys.getInt(1);
                            }
                        }
                    }
                }

                // 2. Insert Registration
                int registrationId = 0;
                String insertRegSql = "INSERT INTO registrations (student_id, event_id, status) VALUES (?, ?, 'CONFIRMED')";
                try (PreparedStatement psReg = conn.prepareStatement(insertRegSql, Statement.RETURN_GENERATED_KEYS)) {
                    psReg.setInt(1, studentId);
                    psReg.setInt(2, eventId);
                    psReg.executeUpdate();
                    try (ResultSet rsRegKeys = psReg.getGeneratedKeys()) {
                        if (rsRegKeys.next()) {
                            registrationId = rsRegKeys.getInt(1);
                        }
                    }
                }

                conn.commit(); // Commit transaction
                return registrationId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Retrieves all registrations with joined student and event details.
     */
    public List<Registration> getAllRegistrations() throws SQLException {
        return queryRegistrations(null, null);
    }

    /**
     * Retrieves registrations filtered by event title.
     */
    public List<Registration> getRegistrationsByEventTitle(String eventTitle) throws SQLException {
        return queryRegistrations("e.title = ?", eventTitle);
    }

    private List<Registration> queryRegistrations(String whereClause, String param) throws SQLException {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT r.id, r.student_id, s.roll_no, s.name AS student_name, s.email, s.department, " +
                     "r.event_id, e.title AS event_title, r.registered_at, r.status " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN events e ON r.event_id = e.id ";
        if (whereClause != null) {
            sql += "WHERE " + whereClause + " ";
        }
        sql += "ORDER BY r.registered_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("roll_no"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getInt("event_id"),
                        rs.getString("event_title"),
                        rs.getTimestamp("registered_at"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
}