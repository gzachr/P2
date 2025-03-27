import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonInstance implements Runnable{
    private static final Random random = new Random();
    private boolean active;
    private final int id;
    private AtomicInteger partiesServed = new AtomicInteger(0);
    private AtomicInteger totalRunningTime = new AtomicInteger(0);
    private final int t1;
    private final int t2;

    public DungeonInstance(int id, int min, int max) {
        this.active = false;
        this.id = id;
        this.t1 = min;
        this.t2 = max;
    }

    @Override
    public void run() {
        try {
            int dungeonTime = random.nextInt(t2 - t1 + 1) + t1;

            System.out.println("[Instance " + id + "] Active | Party will take " + dungeonTime + "s to clear.");
            TimeUnit.SECONDS.sleep(dungeonTime);

            synchronized (this) {
                active = false;
                partiesServed.incrementAndGet();
                totalRunningTime.addAndGet(dungeonTime);
            }
            System.out.println("[Instance " + id + "] Finished serving party. Now empty.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void start() {
        active = true;
        new Thread(this).start();
    }

    public synchronized boolean isAvailable() {
        return !active;
    }

    public int getId() {
        return id;
    }

    public synchronized int getPartiesServed() {
        return partiesServed.get();
    }

    public synchronized int getTotalRunningTime() {
        return totalRunningTime.get();
    }

}
