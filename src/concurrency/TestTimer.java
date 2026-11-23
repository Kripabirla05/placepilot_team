package concurrency;

public class TestTimer extends Thread {

    private final int seconds;
    private volatile boolean timeUp = false;

    public TestTimer(int seconds) {
        super("TestTimer");
        this.seconds = seconds;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(seconds * 1000L);
            timeUp = true;
            System.out.println("\n*** TIME IS UP! Your next answer will not be counted. ***");
        } catch (InterruptedException e) {

        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }
}
