import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

//        STACK TESTING

//        VersionableStack<Integer> stack = new VersionableStack<Integer>(ResolveStrategy.LEFT);
//
//        stack.add(2);
//        stack.add(3);
//
//        executor.submit(() -> {
//            System.out.println(stack.pop()); // sleep 2000
//            System.out.println(stack.toString());
//
//        });
//
//        executor.submit(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            stack.pop();
//            stack.add(5);
//            System.out.println(stack.toString());
//        });

//        QUEUE TESTING

//        VersionableQueue<Integer> queue = new VersionableQueue<Integer>(ResolveStrategy.LEFT_RIGHT);
//
//        queue.add(2);
//        queue.add(3);
//
//        executor.submit(() -> {
//            System.out.println(queue.remove()); // sleep 2000
//            System.out.println(queue.toString());
//
//        });
//
//        executor.submit(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            queue.remove();
//            queue.add(5);
//            System.out.println(queue.toString());
//        });
//
//    }

//        SET TESTING

        VersionableSet<Integer> set = new VersionableSet<Integer>(ResolveStrategy.RIGHT);

        System.out.println(set.add(2));
        System.out.println(set.add(2));

//        executor.submit(() -> {
//            System.out.println(set.remove(2)); // sleep 2000
//            System.out.println(set.toString());
//
//        });
//
//        executor.submit(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println(set.remove(3));
//            System.out.println(set.add(5));
//            System.out.println(set.toString());
//        });

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