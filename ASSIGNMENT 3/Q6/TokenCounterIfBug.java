package q6;

/**
 * Question 6: Demonstrates what happens when 'while' is changed to 'if' in TokenCounter.
 * 
 * If 'if' is used instead of 'while':
 * When multiple threads are waiting, a notifyAll() wakes up multiple consumers.
 * With 'if', threads DO NOT re-check the condition (count == 0) upon waking!
 * Thread 1 takes the token, reducing count to 0.
 * Thread 2 resumes immediately without re-checking, attempts to take from empty buffer,
 * producing corrupted count, data duplication, or ArrayIndexOutOfBoundsException!
 */
public class TokenCounterIfBug {
    private final int[] buffer = new int[5];
    private int count = 0, in = 0, out = 0;
    public boolean errorDetected = false;

    // FLAWED: uses 'if' instead of 'while'
    public synchronized void putIf(int token) throws InterruptedException {
        if (count == buffer.length) {
            wait();
        }
        if (count >= buffer.length) {
            System.err.println("[BUG DETECTED] Buffer overflow! Put attempted when count = " + count);
            errorDetected = true;
        }
        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;
        notifyAll();
    }

    // FLAWED: uses 'if' instead of 'while'
    public synchronized int takeIf() throws InterruptedException {
        if (count == 0) {
            wait();
        }
        if (count <= 0) {
            System.err.println("[BUG DETECTED] Buffer underflow! Take attempted when count = " + count);
            errorDetected = true;
        }
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return token;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("============================================================");
        System.out.println("    EVIDENCE: CHANGING 'while' TO 'if' IN TokenCounter     ");
        System.out.println("============================================================");

        TokenCounterIfBug bugDemo = new TokenCounterIfBug();
        int[] collected = new int[3];

        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= 30; t++) {
                    bugDemo.putIf(t);
                    Thread.sleep(1);
                }
            } catch (InterruptedException ignored) {}
        });

        // 3 consumers competing - high chance of spurious/competing wakeup
        Thread[] students = new Thread[3];
        for (int s = 0; s < 3; s++) {
            final int id = s;
            students[s] = new Thread(() -> {
                try {
                    for (int k = 0; k < 10; k++) {
                        collected[id] += bugDemo.takeIf();
                    }
                } catch (InterruptedException ignored) {}
            });
        }

        cook.start();
        for (Thread t : students) t.start();
        cook.join();
        for (Thread t : students) t.join();

        System.out.println("Sum of tokens collected with 'if': " + (collected[0] + collected[1] + collected[2]));
        System.out.println("Underflow / Corruption Detected: " + bugDemo.errorDetected);
        System.out.println("Reason: 'while' loop guards against spurious wakeups and state theft by competitor threads.");
        System.out.println("============================================================");
    }
}
