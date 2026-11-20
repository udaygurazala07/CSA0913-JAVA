package q5;

/**
 * Question 5: Complete the Code - SeatBooking
 * Completes TODO 1: Make the check-then-book operation atomic using synchronized
 * so that a seat can never be sold twice.
 * 
 * Expected output:
 * Seats sold: 10
 */
public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    // TODO 1: Make the check-then-book operation atomic so that a seat can never be
    // sold twice
    public synchronized boolean book(int seat) {
        if (!booked[seat]) {
            try {
                Thread.sleep(1); // simulate realistic I/O latency
            } catch (InterruptedException ignored) {}
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];

        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10; // 10 users compete for every seat
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }

        for (Thread t : users) t.join();
        System.out.println("Seats sold: " + screen.bookingsMade);
    }
}
