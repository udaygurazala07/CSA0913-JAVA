package task06.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import task06.model.Event;

/**
 * Data Access Object for Event management.
 */
public class EventDAO {

    public List<Event> getAllEvents() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT id, title, category, event_date, venue, capacity FROM events ORDER BY event_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Event(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("category"),
                    rs.getString("event_date"),
                    rs.getString("venue"),
                    rs.getInt("capacity")
                ));
            }
        }
        return list;
    }

    public Event getEventById(int id) throws SQLException {
        String sql = "SELECT id, title, category, event_date, venue, capacity FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("event_date"),
                        rs.getString("venue"),
                        rs.getInt("capacity")
                    );
                }
            }
        }
        return null;
    }
}