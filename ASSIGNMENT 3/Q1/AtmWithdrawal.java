package q1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Question 1: Handling Failure in a Bank ATM Simulation
 * 
 * Demonstrates:
 * - Exception hierarchy (Checked vs Unchecked vs Error)
 * - Specific catch blocks and multi-catch (disjoint types per JLS 14.20)
 * - finally block guarantee for hardware/session cleanup
 * - Stack trace inspection and analysis
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class AtmWithdrawal {

    // Custom checked exception for business logic failure
    public static class InsufficientFundsException extends Exception {
        private final double balance;
        private final double requested;

        public InsufficientFundsException(String message, double balance, double requested) {
            super(message);
            this.balance = balance;
            this.requested = requested;
        }

        public double getBalance() { return balance; }
        public double getRequested() { return requested; }
    }

    // Custom checked exception for network/security failure
    public static class AtmSecurityException extends Exception {
        public AtmSecurityException(String message) {
            super(message);
        }
    }

    // Hardware Card Reader simulator
    public static class CardReader {
        public static String readCardNumber(String cardConfigPath) throws FileNotFoundException {
            File file = new File(cardConfigPath);
            if (!file.exists()) {
                throw new FileNotFoundException("Card-reader hardware interface file not found: " + cardConfigPath);
            }
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    return scanner.nextLine().trim();
                } else {
                    throw new IllegalArgumentException("Card data is corrupted or empty in: " + cardConfigPath);
                }
            }
        }
    }

    // Account state
    private final String accountNumber;
    private double balance;

    public AtmWithdrawal(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Executes withdrawal transaction with robust exception handling.
     * Returns a summary status string indicating which catch block or success path was triggered.
     */
    public String withdraw(String cardFilePath, String amountInput) {
        String outcome = "";
        String catchBlockExecuted = "NONE";
        boolean finallyExecuted = false;

        System.out.println("\n------------------------------------------------------------");
        System.out.println("Processing Withdrawal Request: [Card File: " + cardFilePath + ", Amount: '" + amountInput + "']");

        try {
            // Step 1: Read card reader hardware interface (Checked Exception: FileNotFoundException)
            String verifiedCard = CardReader.readCardNumber(cardFilePath);
            System.out.println("[INFO] Card authenticated successfully. Card Number: " + verifiedCard);

            // Step 2: Parse amount input (Unchecked Exception: NumberFormatException)
            double amount;
            try {
                amount = Double.parseDouble(amountInput);
            } catch (NumberFormatException nfe) {
                // Re-throw to be caught by outer catch
                throw new NumberFormatException("Invalid amount format: '" + amountInput + "'. Numbers only.");
            }

            // Step 3: Validate amount range (Unchecked Exception: IllegalArgumentException)
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be strictly positive. Entered: " + amount);
            }

            // Step 4: Check business rule - balance limit (Checked Exception: InsufficientFundsException)
            if (amount > this.balance) {
                throw new InsufficientFundsException(
                    "Insufficient funds. Requested: Rs. " + amount + ", Available: Rs. " + this.balance,
                    this.balance,
                    amount
                );
            }

            // Step 5: Dispense cash and update balance
            this.balance -= amount;
            System.out.println("[SUCCESS] Dispensed: Rs. " + amount + " | Remaining Balance: Rs. " + this.balance);
            outcome = "SUCCESS";

        } catch (NumberFormatException nfe) {
            catchBlockExecuted = "catch (NumberFormatException)";
            System.out.println("[CATCH] " + catchBlockExecuted + " -> " + nfe.getMessage());
            outcome = "FAILED_NUMBER_FORMAT";

        } catch (IllegalArgumentException iae) {
            catchBlockExecuted = "catch (IllegalArgumentException)";
            System.out.println("[CATCH] " + catchBlockExecuted + " -> " + iae.getMessage());
            outcome = "FAILED_ILLEGAL_AMOUNT";

        } catch (FileNotFoundException | InsufficientFundsException checkedEx) {
            // Multi-catch demonstrating handling of disjoint checked failure paths (JLS §14.20)
            catchBlockExecuted = "catch (FileNotFoundException | InsufficientFundsException)";
            System.out.println("[CATCH] " + catchBlockExecuted + " -> " + checkedEx.getMessage());
            if (checkedEx instanceof FileNotFoundException) {
                outcome = "FAILED_CARD_READ";
            } else {
                outcome = "FAILED_INSUFFICIENT_FUNDS";
            }

        } catch (Exception e) {
            catchBlockExecuted = "catch (Exception e) [Fallback]";
            System.out.println("[CATCH] " + catchBlockExecuted + " -> Unexpected failure: " + e.getMessage());
            outcome = "FAILED_UNKNOWN";

        } finally {
            finallyExecuted = true;
            System.out.println("[FINALLY] Cleanup executed: Ejected card, logged session to audit, cleared PIN buffer.");
        }

        return outcome + "|" + catchBlockExecuted + "|" + finallyExecuted;
    }

    /**
     * Test harness running 5 required scenarios and exporting results to CSV.
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  BANK ATM SIMULATION - EXCEPTION HANDLING BENCHMARK & TEST ");
        System.out.println("  Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        // Prepare valid card file
        String validCardFile = "valid_card.txt";
        String missingCardFile = "missing_card_reader.txt";
        try (FileWriter writer = new FileWriter(validCardFile)) {
            writer.write("CARD-SB-9876543210\n");
        } catch (IOException e) {
            System.err.println("Could not create mock card file: " + e.getMessage());
        }

        AtmWithdrawal atm = new AtmWithdrawal("ACC-192311094", 25000.00);

        // 5 Test Scenarios required by the assignment
        String[][] testCases = {
            {"Test 1: Valid Withdrawal", validCardFile, "5000", "Normal valid cash withdrawal"},
            {"Test 2: Non-numeric Input", validCardFile, "five_thousand", "Letters typed instead of numbers"},
            {"Test 3: Negative Amount", validCardFile, "-1500", "Negative withdrawal value entered"},
            {"Test 4: Insufficient Funds", validCardFile, "35000", "Amount exceeding available account balance"},
            {"Test 5: Missing Card Reader", missingCardFile, "2000", "Card reader file or hardware config missing"}
        };

        StringBuilder csvData = new StringBuilder();
        csvData.append("TestCase,Description,InputFile,InputAmount,Outcome,CatchBlockExecuted,FinallyExecuted\n");

        for (String[] tc : testCases) {
            String testName = tc[0];
            String cardFile = tc[1];
            String amount = tc[2];
            String description = tc[3];

            String result = atm.withdraw(cardFile, amount);
            String[] parts = result.split("\\|");
            String outcome = parts[0];
            String catchBlock = parts[1];
            String finallyRun = parts[2];

            csvData.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s\n",
                    testName, description, cardFile, amount, outcome, catchBlock, finallyRun));
        }

        // Save CSV results
        String csvPath = "q1_results.csv";
        try (FileWriter fw = new FileWriter(csvPath)) {
            fw.write(csvData.toString());
            System.out.println("\n[INFO] Results successfully saved to: " + csvPath);
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.println("Final Account Balance: Rs. " + atm.getBalance());
        System.out.println("============================================================");
    }
}
