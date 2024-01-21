import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;


public class VersionableQueue<E> {

    private LinkedList<E> queue;
    private Integer version;

    private final ResolveStrategy strategy;

    public VersionableQueue(ResolveStrategy strategy) {
        this.queue = new LinkedList<>();
        this.strategy = strategy;
        this.version = 0;
    }

    private VersionableQueue(LinkedList<E> s, Integer version, ResolveStrategy strategy) {
        this.queue = s;
        this.version = version;
        this.strategy = strategy;
    }
    @SuppressWarnings("unchecked")
    private VersionableQueue<E> fork() {
        LinkedList<E> newQueue = (LinkedList<E>)queue.clone();
        return new VersionableQueue<E>(newQueue, version + 1, strategy);
    }

    private boolean merge(VersionableQueue<E> master,
                       VersionableQueue<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.version - 1, master.version)) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return false; }
                case RIGHT -> {
                    master.queue = revised.queue;
                    master.version += 1;
                    return true;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }

            return false;
        }
        master.queue = revised.queue;
        master.version += 1;
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
                ", version = " + version +
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