import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

//        STACK TESTING

//        VersionableStack<Integer> stack = new VersionableStack<Integer>(ResolveStrategy.LEFT);
//
//        stack.push(intWithSleep(2, 2000));
//        stack.push(intWithSleep(3, 2000));
//
//        executor.submit(() -> {
//            stack.push(intWithSleep(4, 2000));
//            System.out.println(stack.toString());
//
//        });
//
//        executor.submit(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            stack.push(intWithSleep(5, 1000));
//            System.out.println(stack.toString());
//        });

//        VersionableStack<Integer> stack2 = new VersionableStack<Integer>(ResolveStrategy.LEFT);
//
//        stack2.push(intWithSleep(2, 2000));
//        stack2.push(intWithSleep(3, 2000));
//
//        executor.submit(() -> {
//            stack2.push(intWithSleep(stack2.search(2), 2000));
//            System.out.println(stack2.toString());
//
//        });

//        executor.submit(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            stack2.push(intWithSleep(5, 1000));
//            System.out.println(stack2.toString());
//        });

//        QUEUE TESTING

        VersionableQueue<Integer> queue = new VersionableQueue<Integer>(ResolveStrategy.LEFT_RIGHT);
//
        queue.add(2);
        queue.add(3);



        executor.submit(() -> {
            queue.add(() -> {
                queue.add(intWithSleep(4, 2000));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return queue.peek();
            });
//            queue.add(intWithSleep(4, 2000));
        });
//
//        executor.submit(() -> {
//            queue.add(intWithSleep(5, 3000));
//        });


//        SET TESTING

        VersionableSet<Integer> set = new VersionableSet<Integer>(ResolveStrategy.RIGHT);
//
//        System.out.println(set.add(2));
//        System.out.println(set.add(2));
//
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
//
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