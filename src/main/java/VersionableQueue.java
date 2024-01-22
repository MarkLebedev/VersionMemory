import java.util.*;


public class VersionableQueue<E> {

    private LinkedList<E> queue;
    private Integer masterState;
    private Integer state() { return queue.hashCode(); }

    private final ResolveStrategy strategy;

    public VersionableQueue(ResolveStrategy strategy) {
        this.queue = new LinkedList<>();
        this.strategy = strategy;
        this.masterState = queue.hashCode();
    }

    private VersionableQueue(LinkedList<E> s, Integer masterState, ResolveStrategy strategy) {
        this.queue = s;
        this.masterState = masterState;
        this.strategy = strategy;
    }
    @SuppressWarnings("unchecked")
    private VersionableQueue<E> fork() {
        LinkedList<E> newQueue = (LinkedList<E>)queue.clone();
        return new VersionableQueue<E>(newQueue, this.state(), strategy);
    }

    private synchronized boolean merge(VersionableQueue<E> master,
                       VersionableQueue<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.masterState, master.state())) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return false; }
                case RIGHT -> {
                    master.queue = revised.queue;
                    return true;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }

            return false;
        }
        master.queue = revised.queue;
        return true;
    }

    public boolean add(E obj) {

        VersionableQueue<E> copyQueue = fork();

        copyQueue.queue.offer(obj);

        return merge(this, copyQueue, () -> { this.add(obj); });
    }

    public boolean add(VersionableFunction<E> function) {

        VersionableQueue<E> copyQueue = fork();

        E obj = function.apply();

        copyQueue.queue.offer(obj);

        return merge(this, copyQueue, () -> { this.add(obj); });
    }

    @Override
    public String toString() {
        return "VersionableQueue { " +
                "queue = " + queue +
                " masterState = " + masterState +
                " state = " + state() +
                " }";
    }
    public E remove() {

        VersionableQueue<E> copyQueue = fork();

        var object = copyQueue.queue.poll();

        merge(this, copyQueue, this::remove);

        return object;
    }

    public E poll() {

        if (this.queue.isEmpty()) {
            return null;
        }

        return this.queue.remove();
    }

    public E peek() {
        return this.queue.peek();
    }

    public E element() {
        return this.queue.element();
    }

    

}