package q1;

/**
 * Alternative Concept for Q1:
 * Avoiding exceptions altogether by using defensive validation (Scanner checks, if-else guards)
 * and returning error status codes (-1, -2, -3) instead of throwing exceptions.
 * 
 * Also benchmarks the runtime overhead of exception creation vs error codes over 100,000 iterations.
 */
public class AtmErrorCodesAlternative {

    public static final int SUCCESS = 0;
    public static final int ERR_INVALID_FORMAT = -1;
    public static final int ERR_NEGATIVE_AMOUNT = -2;
    public static final int ERR_INSUFFICIENT_FUNDS = -3;
    public static final int ERR_HARDWARE_OFFLINE = -4;

    private double balance;

    public AtmErrorCodesAlternative(double initialBalance) {
        this.balance = initialBalance;
    }

    // Defensive validation using error codes
    public int withdrawWithErrorCode(boolean cardReaderOnline, String amountStr) {
        if (!cardReaderOnline) {
            return ERR_HARDWARE_OFFLINE;
        }

        // Check if string contains only valid digits/decimals without throwing NumberFormatException
        if (amountStr == null || !amountStr.matches("^[0-9]+(\\.[0-9]+)?$")) {
            return ERR_INVALID_FORMAT;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            return ERR_NEGATIVE_AMOUNT;
        }

        if (amount > this.balance) {
            return ERR_INSUFFICIENT_FUNDS;
        }

        this.balance -= amount;
        return SUCCESS;
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   ALTERNATIVE CONCEPT BENCHMARK: ERROR CODES VS EXCEPTIONS ");
        System.out.println("============================================================");

        AtmErrorCodesAlternative atmErr = new AtmErrorCodesAlternative(1000000);
        int iterations = 100_000;

        // 1. Benchmark Error Code path on failure
        long startErr = System.nanoTime();
        int errCount = 0;
        for (int i = 0; i < iterations; i++) {
            int code = atmErr.withdrawWithErrorCode(true, "invalid_num");
            if (code != SUCCESS) {
                errCount++;
            }
        }
        long timeErr = System.nanoTime() - startErr;

        // 2. Benchmark Exception path on failure
        long startEx = System.nanoTime();
        int exCount = 0;
        for (int i = 0; i < iterations; i++) {
            try {
                // Parse failure triggers NumberFormatException
                Double.parseDouble("invalid_num");
            } catch (NumberFormatException e) {
                exCount++;
            }
        }
        long timeEx = System.nanoTime() - startEx;

        System.out.printf("Iterations: %,d\n", iterations);
        System.out.printf("Error Codes Execution Time : %,12d ns (%.2f ms)\n", timeErr, timeErr / 1_000_000.0);
        System.out.printf("Exceptions Execution Time  : %,12d ns (%.2f ms)\n", timeEx, timeEx / 1_000_000.0);
        double overhead = (double) timeEx / (timeErr == 0 ? 1 : timeErr);
        System.out.printf("Exception Overhead Ratio   : %.2fx slower than error codes\n", overhead);
        System.out.println("Reason: Exception creation populates the execution stack trace via Throwable.fillInStackTrace().");
        System.out.println("============================================================");
    }
}
