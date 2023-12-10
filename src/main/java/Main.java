import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        VersionableStack<Integer> stack = new VersionableStack<Integer>(ResolveStrategy.LEFT);

        stack.add(2);
        stack.add(3);

        executor.submit(() -> {
            System.out.println(stack.pop()); // sleep 2000
            System.out.println(stack.toString());

        });

        executor.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stack.pop();
            stack.add(5);
            System.out.println(stack.toString());
        });


    }

    static private Integer intWithoutSleep(Integer value) {
        return value;
    }

    static private Integer intWithSleep(Integer value, Integer sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            System.out.println(e.fillInStackTrace());
        }
        return value;
    }


}