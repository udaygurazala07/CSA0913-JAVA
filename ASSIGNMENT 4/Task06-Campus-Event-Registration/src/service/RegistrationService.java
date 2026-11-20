package task06.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import task06.dao.EventDAO;
import task06.dao.RegistrationDAO;
import task06.model.Event;
import task06.model.Registration;
import task06.model.Student;

/**
 * Service orchestrating business transactions, duplicate checks, and data export.
 */
public class RegistrationService {
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final ValidationService validationService = new ValidationService();

    public static class DuplicateRegistrationException extends Exception {
        public DuplicateRegistrationException(String message) { super(message); }
    }

    public List<Event> getAllEvents() throws SQLException {
        return eventDAO.getAllEvents();
    }

    public List<Registration> getAllRegistrations() throws SQLException {
        return registrationDAO.getAllRegistrations();
    }

    public List<Registration> getRegistrationsByEventTitle(String title) throws SQLException {
        if (title == null || title.equalsIgnoreCase("All Events")) {
            return registrationDAO.getAllRegistrations();
        }
        return registrationDAO.getRegistrationsByEventTitle(title);
    }

    public int register(String rollNo, String name, String email,
                        String dept, int year, Event event)
            throws ValidationService.ValidationException, DuplicateRegistrationException, SQLException {

        // 1. Validation
        validationService.validateRegistration(rollNo, name, email, dept, year, event);

        // 2. Duplicate Check
        if (registrationDAO.isStudentRegistered(rollNo, event.getId())) {
            throw new DuplicateRegistrationException(
                "Duplicate Registration Rejected:\nStudent [" + rollNo + "] is already registered for:\n\"" + event.getTitle() + "\""
            );
        }

        // 3. Persistence
        Student student = new Student(rollNo, name, email, dept, year);
        return registrationDAO.register(student, event.getId());
    }

    public void exportToCSV(List<Registration> records, File targetFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            // Write CSV Header
            writer.println("Registration_ID,Student_Roll,Student_Name,Email,Department,Event_Title,Registration_Date,Status");

            // Write Data Rows
            for (Registration r : records) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    r.getId(),
                    escapeCsv(r.getStudentRoll()),
                    escapeCsv(r.getStudentName()),
                    escapeCsv(r.getStudentEmail()),
                    escapeCsv(r.getStudentDept()),
                    escapeCsv(r.getEventTitle()),
                    r.getFormattedDate(),
                    r.getStatus()
                );
            }
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\"");
    }
}