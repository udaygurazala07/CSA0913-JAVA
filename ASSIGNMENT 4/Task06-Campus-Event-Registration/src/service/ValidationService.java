package task06.service;

import java.util.regex.Pattern;
import task06.model.Event;

/**
 * Service dedicated to pre-flight data validation.
 */
public class ValidationService {
    private static final Pattern ROLL_PATTERN = Pattern.compile("^[0-9A-Za-z]{6,12}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    public void validateRegistration(String rollNo, String name, String email,
                                     String department, int year, Event event) throws ValidationException {
        if (rollNo == null || rollNo.trim().isEmpty()) {
            throw new ValidationException("Student Roll Number is required.");
        }
        if (!ROLL_PATTERN.matcher(rollNo.trim()).matches()) {
            throw new ValidationException("Invalid Roll Number. Must be 6-12 alphanumeric characters (e.g., 192311094).");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Student Full Name is required.");
        }
        if (name.trim().length() < 3) {
            throw new ValidationException("Student Full Name must be at least 3 characters.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid Email format (e.g., student@simats.edu).");
        }

        if (department == null || department.trim().isEmpty() || "Select Department".equalsIgnoreCase(department)) {
            throw new ValidationException("Please select a valid academic department.");
        }

        if (year < 1 || year > 4) {
            throw new ValidationException("Academic Year must be between 1 and 4.");
        }

        if (event == null) {
            throw new ValidationException("Please select a campus event to register for.");
        }
    }
}