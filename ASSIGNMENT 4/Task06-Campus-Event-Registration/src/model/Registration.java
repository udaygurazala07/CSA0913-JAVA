package task06.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Model representing an event registration record.
 */
public class Registration {
    private int id;
    private int studentId;
    private String studentRoll;
    private String studentName;
    private String studentEmail;
    private String studentDept;
    private int eventId;
    private String eventTitle;
    private Timestamp registeredAt;
    private String status;

    public Registration(int id, int studentId, String studentRoll, String studentName,
                        String studentEmail, String studentDept, int eventId,
                        String eventTitle, Timestamp registeredAt, String status) {
        this.id = id;
        this.studentId = studentId;
        this.studentRoll = studentRoll;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentDept = studentDept;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentRoll() { return studentRoll; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentDept() { return studentDept; }
    public int getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public Timestamp getRegisteredAt() { return registeredAt; }
    public String getStatus() { return status; }

    public String getFormattedDate() {
        if (registeredAt == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(registeredAt);
    }
}