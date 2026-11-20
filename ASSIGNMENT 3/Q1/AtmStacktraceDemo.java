package q1;

/**
 * Demonstrates an uncaught exception stack trace in the ATM simulation
 * for Student B's stack trace dissection requirement.
 */
public class AtmStacktraceDemo {

    public static void processTransaction(String input) {
        validatePin(input);
    }

    public static void validatePin(String pin) {
        checkHardware(pin);
    }

    public static void checkHardware(String pin) {
        if (pin == null) {
            throw new NullPointerException("Hardware card chip PIN buffer is null at slot 0");
        }
    }

    public static void main(String[] args) {
        System.out.println("Executing AtmStacktraceDemo to produce stack trace evidence:");
        processTransaction(null);
    }
}
