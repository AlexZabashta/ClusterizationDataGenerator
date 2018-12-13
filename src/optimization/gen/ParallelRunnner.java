package optimization.gen;

import java.util.ArrayList;
import java.util.List;

public class ParallelRunnner {

    public static void run(List<Runnable> tasks) {
        List<Thread> threads = new ArrayList<Thread>(tasks.size());

        for (Runnable runnable : tasks) {
            threads.add(new Thread(runnable));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
