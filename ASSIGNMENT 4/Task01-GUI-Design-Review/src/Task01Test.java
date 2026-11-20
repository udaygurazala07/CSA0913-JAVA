package task01;

public class Task01Test {
    public static void main(String[] args) {
        System.out.println("=== TASK 1: FEE SERVICE AUTOMATED TEST SUITE ===");
        String dbUrl = "jdbc:mysql://localhost:3306/collegedb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
        FeeService service = new FeeService(dbUrl, "root", "Dhanush7184");

        // Test 1: Empty Reg No
        try {
            service.validate("", "5000");
            System.out.println("[FAIL] Test 1: Empty RegNo should have failed.");
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 1: Successfully caught empty RegNo: " + e.getMessage());
        }

        // Test 2: Invalid Amount String
        try {
            service.validate("192311094", "five_thousand");
            System.out.println("[FAIL] Test 2: Non-numeric amount should have failed.");
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 2: Successfully caught non-numeric amount: " + e.getMessage());
        }

        // Test 3: Negative Amount
        try {
            service.validate("192311094", "-250");
            System.out.println("[FAIL] Test 3: Negative amount should have failed.");
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 3: Successfully caught negative amount: " + e.getMessage());
        }

        // Test 4: Valid Payment Execution
        try {
            service.validate("192311094", "45000.00");
            System.out.println("[PASS] Test 4a: Validation passed for 192311094, 45000.00");
            String receipt = service.processPayment("192311094", 45000.00);
            System.out.println("[PASS] Test 4b: Payment processed successfully! Details: " + receipt);
        } catch (Exception e) {
            System.out.println("[FAIL] Test 4: Payment execution failed: " + e.getMessage());
        }

        System.out.println("=== ALL TASK 1 SERVICE TESTS COMPLETED ===");
    }
}