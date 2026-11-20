package q4;

import java.util.Random;

/**
 * Question 4: Scanner Station implementation extending Thread.
 * 
 * Demonstrates:
 * - Thread subclassing
 * - start(), sleep(), join(), and isAlive()
 * - Reporting scanned batch totals
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ScannerStationThread extends Thread {
    private final int stationId;
    private final int scriptsToScan;
    private int scriptsScanned = 0;
    private final Random random = new Random();

    public ScannerStationThread(int stationId, int scriptsToScan) {
        super("ScannerStation-Thread-" + stationId);
        this.stationId = stationId;
        this.scriptsToScan = scriptsToScan;
    }

    public int getScriptsScanned() {
        return scriptsScanned;
    }

    public int getStationId() {
        return stationId;
    }

    @Override
    public void run() {
        System.out.printf("[%s] Station %d started scanning %d scripts...\n",
                getName(), stationId, scriptsToScan);

        for (int i = 1; i <= scriptsToScan; i++) {
            // Simulate variable scanning latency per script (5 to 15 ms)
            try {
                Thread.sleep(5 + random.nextInt(11));
            } catch (InterruptedException e) {
                System.err.printf("[%s] Station %d interrupted!\n", getName(), stationId);
                Thread.currentThread().interrupt();
                break;
            }
            scriptsScanned++;
        }

        System.out.printf("[%s] Station %d FINISHED scanning. Total: %d scripts.\n",
                getName(), stationId, scriptsScanned);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("  SCANNER STATIONS: EXTENDING java.lang.Thread             ");
        System.out.println("  Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        int numStations = 4;
        int scriptsPerStation = 50;
        ScannerStationThread[] stations = new ScannerStationThread[numStations];

        // 1. Creation State: NEW
        for (int i = 0; i < numStations; i++) {
            stations[i] = new ScannerStationThread(i + 1, scriptsPerStation);
            System.out.printf("Station %d created. State: %s | isAlive: %b\n",
                    stations[i].getStationId(), stations[i].getState(), stations[i].isAlive());
        }

        // 2. Transition to RUNNABLE using start()
        System.out.println("\n--- Starting All Scanner Threads ---");
        for (ScannerStationThread station : stations) {
            station.start();
        }

        // 3. Inspect states while active
        Thread.sleep(10); // Let them transition into sleep (TIMED_WAITING)
        System.out.println("\n--- Active Inspection of Running Threads ---");
        for (ScannerStationThread station : stations) {
            System.out.printf("Station %d | State: %s | isAlive: %b\n",
                    station.getStationId(), station.getState(), station.isAlive());
        }

        // 4. Coordination via join()
        System.out.println("\n--- Main Thread Waiting on join() for All Stations ---");
        int grandTotal = 0;
        for (ScannerStationThread station : stations) {
            station.join(); // Blocks main thread until this station terminates
            grandTotal += station.getScriptsScanned();
            System.out.printf("Joined Station %d. Final State: %s | isAlive: %b\n",
                    station.getStationId(), station.getState(), station.isAlive());
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("ALL STATIONS TERMINATED. Grand Total Scripts Scanned: %d (Expected: %d)\n",
                grandTotal, numStations * scriptsPerStation);
        System.out.println("============================================================");
    }
}
