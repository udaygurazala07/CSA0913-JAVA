package task01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * FeeService encapsulates all business validation and database operations
 * for the fee payment system. It is completely decoupled from the GUI presentation layer.
 */
public class FeeService {
    private final String url;
    private final String username;
    private final String password;

    public FeeService(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        initializeDatabase();
    }

    /**
     * Ensures the required database and fees table exist.
     */
    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS fees (" +
                             "id INT AUTO-INCREMENT PRIMARY KEY, " +
                             "reg_no VARCHAR(50) NOT NULL, " +
                             "amount DOUBLE NOT NULL, " +
                             "paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                             ")";
                // For MySQL compatibility without syntax error on hyphen:
                String mysqlTable = "CREATE TABLE IF NOT EXISTS fees (" +
                                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "reg_no VARCHAR(50) NOT NULL, " +
                                    "amount DOUBLE NOT NULL, " +
                                    "paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                    ")";
                stmt.executeUpdate(mysqlTable);
            }
        } catch (Exception e) {
            System.err.println("Database initialization warning: " + e.getMessage());
        }
    }

    /**
     * Validates input fields according to business rules.
     */
    public void validate(String regNo, String amountStr) throws IllegalArgumentException {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee amount cannot be empty.");
        }
        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Fee amount must be greater than zero.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format. Please enter a valid numeric value.");
        }
    }

    /**
     * Processes payment securely using PreparedStatement and simulates network confirmation.
     * Throws SQLException or InterruptedException upon failure.
     */
    public String processPayment(String regNo, double amount) throws SQLException, InterruptedException {
        // Parameterized query prevents SQL injection attacks
        String insertSql = "INSERT INTO fees (reg_no, amount) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, regNo.trim());
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        }

        // Simulate third-party bank confirmation delay safely on background thread
        Thread.sleep(1500);

        return "PAYMENT_SUCCESS: Receipt #" + System.currentTimeMillis() + 
               " | RegNo: " + regNo.trim() + " | Amount: INR " + String.format("%.2f", amount);
    }
}
