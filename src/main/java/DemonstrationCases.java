import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class DemonstrationCases {

    public static void queueTest(ResolveStrategy strategy){
        System.out.println("---Queue Demonstration---");

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        VersionableQueue<Integer> queue = new VersionableQueue<Integer>(strategy);

        queue.add(2);
        queue.add(3);

        executor.submit(() -> {
            queue.add(intWithSleep(4, 2000));
        });

        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            queue.add(intWithSleep(5, 2000));
            System.out.println(queue);
        });



        executor.close();

    }

    public static void setTest(ResolveStrategy strategy){
        System.out.println("---Set Demonstration---");

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        VersionableSet<Integer> set = new VersionableSet<Integer>(strategy);

        set.add(2);
        set.add(3);

        executor.submit(() -> {
            set.add(intWithSleep(4, 2000));
        });

        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            set.add(intWithSleep(5, 2000));
            System.out.println(set);
        });

        executor.close();

    }

    public static void stackTest(ResolveStrategy strategy){
        System.out.println("---Stack Demonstration---");

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        VersionableStack<Integer> stack = new VersionableStack<Integer>(strategy);

        stack.push(2);
        stack.push(3);

        executor.submit(() -> {
            stack.push(intWithSleep(4, 2000));
        });

        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stack.push(intWithSleep(5, 2000));
            System.out.println(stack);
        });

        executor.close();

    }

    static private VersionableFunction<Integer> intWithSleep(Integer value, Integer sleep) {
        return () -> {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println(e.fillInStackTrace());
            }
            return value;
        };
    }

}
