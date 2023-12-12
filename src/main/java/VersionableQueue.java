import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;


public class VersionableQueue<E> {

    private LinkedList<E> queue;
    private Integer version;

    private ResolveStrategy strategy;

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
    private VersionableQueue<E> fork() {
        LinkedList<E> newQueue = (LinkedList<E>)queue.clone();
        return new VersionableQueue<E>(newQueue, version + 1, strategy);
    }

    private void merge(VersionableQueue<E> master,
                       VersionableQueue<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.version - 1, master.version)) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return; }
                case RIGHT -> {
                    master.queue = revised.queue;
                    master.version += 1;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }

            return;
        }
        master.queue = revised.queue;
        master.version += 1;
    }

    public void add(E obj) {

        VersionableQueue<E> copyQueue = fork();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        copyQueue.queue.offer(obj);

        merge(this, copyQueue, () -> { this.add(obj); });

    }

    @Override
    public String toString() {
        return "VersionableQueue{" +
                "queue=" + queue +
                ", version=" + version +
                '}';
    }
    public E remove() {

        VersionableQueue<E> copyQueue = fork();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var object = copyQueue.queue.poll();

        merge(this, copyQueue, this::remove);

        return object;
    }
//
}