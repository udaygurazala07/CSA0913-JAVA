package q4;

import java.util.Random;

/**
 * Question 4: Scanner Station implementation using Runnable interface.
 * 
 * Demonstrates:
 * - Decoupling task logic (Runnable) from thread execution (Thread)
 * - Thread composition over inheritance
 * - start(), sleep(), join(), isAlive()
 * 
 * Student: Kattiripalli Dhanush (192311094)
 */
public class ScannerStationRunnable implements Runnable {
    private final int stationId;
    private final int scriptsToScan;
    private int scriptsScanned = 0;
    private final Random random = new Random();

    public ScannerStationRunnable(int stationId, int scriptsToScan) {
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
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] Station %d started scanning %d scripts...\n",
                threadName, stationId, scriptsToScan);

        for (int i = 1; i <= scriptsToScan; i++) {
            try {
                Thread.sleep(5 + random.nextInt(11));
            } catch (InterruptedException e) {
                System.err.printf("[%s] Station %d interrupted!\n", threadName, stationId);
                Thread.currentThread().interrupt();
                break;
            }
            scriptsScanned++;
        }

        System.out.printf("[%s] Station %d FINISHED scanning. Total: %d scripts.\n",
                threadName, stationId, scriptsScanned);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("  SCANNER STATIONS: IMPLEMENTING java.lang.Runnable         ");
        System.out.println("  Student: Kattiripalli Dhanush | Register No: 192311094   ");
        System.out.println("============================================================");

        int numStations = 4;
        int scriptsPerStation = 50;
        ScannerStationRunnable[] tasks = new ScannerStationRunnable[numStations];
        Thread[] threads = new Thread[numStations];

        for (int i = 0; i < numStations; i++) {
            tasks[i] = new ScannerStationRunnable(i + 1, scriptsPerStation);
            threads[i] = new Thread(tasks[i], "ScannerRunnable-Thread-" + (i + 1));
            System.out.printf("Station %d task assigned to %s. State: %s\n",
                    tasks[i].getStationId(), threads[i].getName(), threads[i].getState());
        }

        System.out.println("\n--- Starting All Threads ---");
        for (Thread t : threads) {
            t.start();
        }

        int grandTotal = 0;
        for (int i = 0; i < numStations; i++) {
            threads[i].join();
            grandTotal += tasks[i].getScriptsScanned();
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("ALL RUNNABLE STATIONS COMPLETED. Grand Total Scanned: %d\n", grandTotal);
        System.out.println("============================================================");
    }
}
