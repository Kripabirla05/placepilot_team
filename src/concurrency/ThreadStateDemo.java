package concurrency;

import java.util.concurrent.CountDownLatch;

/** Console demonstrations for common Java thread states and synchronization. */
public class ThreadStateDemo {
    private static final int WORKERS = 4;
    private static final int INCREMENTS_PER_WORKER = 100000;

    public static void main(String[] args) throws InterruptedException {
        demonstrateLifecycle();
        demonstrateRaceAndSynchronization();
    }

    private static void demonstrateLifecycle() throws InterruptedException {
        System.out.println("=== Thread lifecycle ===");
        CountDownLatch release = new CountDownLatch(1);
        Thread worker = new Thread(() -> {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "lifecycle-worker");

        System.out.println("Before start: " + worker.getState());
        worker.start();
        awaitState(worker, Thread.State.WAITING);
        System.out.println("Waiting on CountDownLatch: " + worker.getState());
        release.countDown();
        worker.join();
        System.out.println("After join: " + worker.getState());
        System.out.println("NEW -> RUNNABLE -> WAITING -> TERMINATED (the exact RUNNABLE interval is brief).");
    }

    private static void awaitState(Thread thread, Thread.State expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000;
        while (thread.getState() != expected && thread.isAlive() && System.currentTimeMillis() < deadline) {
            Thread.yield();
        }
    }

    private static void demonstrateRaceAndSynchronization() throws InterruptedException {
        System.out.println("\n=== Race condition ===");
        final int[] unsafeCounter = {0};
        CountDownLatch unsafeStart = new CountDownLatch(1);
        Thread[] unsafeWorkers = new Thread[WORKERS];
        for (int i = 0; i < WORKERS; i++) {
            unsafeWorkers[i] = new Thread(() -> {
                awaitStart(unsafeStart);
                for (int n = 0; n < INCREMENTS_PER_WORKER; n++) unsafeCounter[0]++;
            }, "unsafe-counter-" + i);
            unsafeWorkers[i].start();
        }
        unsafeStart.countDown();
        for (Thread worker : unsafeWorkers) worker.join();
        int expected = WORKERS * INCREMENTS_PER_WORKER;
        System.out.println("Unsynchronized increments: " + unsafeCounter[0] + " (expected " + expected + ")");
        System.out.println("Several threads update the same value without a lock, so increments can be lost.");

        System.out.println("\n=== Synchronized access ===");
        final int[] safeCounter = {0};
        Object lock = new Object();
        CountDownLatch safeStart = new CountDownLatch(1);
        Thread[] safeWorkers = new Thread[WORKERS];
        for (int i = 0; i < WORKERS; i++) {
            safeWorkers[i] = new Thread(() -> {
                awaitStart(safeStart);
                for (int n = 0; n < INCREMENTS_PER_WORKER; n++) {
                    synchronized (lock) { safeCounter[0]++; }
                }
            }, "safe-counter-" + i);
            safeWorkers[i].start();
        }
        safeStart.countDown();
        for (Thread worker : safeWorkers) worker.join();
        System.out.println("Synchronized increments: " + safeCounter[0] + " (expected " + expected + ")");
        System.out.println("The synchronized block allows one thread at a time to update the shared counter.");
    }

    private static void awaitStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
