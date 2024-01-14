import java.util.HashSet;
import java.util.Objects;
import java.util.Stack;

public class VersionableSet<E> {

    private HashSet<E> set;
    private Integer version;

    private final ResolveStrategy strategy;

    public VersionableSet(ResolveStrategy strategy) {
        this.set = new HashSet<E>();
        this.strategy = strategy;
        this.version = 0;
    }

    private VersionableSet(HashSet<E> s, Integer version, ResolveStrategy strategy) {
        this.set = s;
        this.version = version;
        this.strategy = strategy;
    }
    @SuppressWarnings("unchecked")
    private VersionableSet<E> fork() {
        HashSet<E> newSet = (HashSet<E>)set.clone();
        return new VersionableSet<E>(newSet, version + 1, strategy);
    }

    private boolean merge(VersionableSet<E> master,
                       VersionableSet<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.version - 1, master.version)) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return false; }
                case RIGHT -> {
                    master.set = revised.set;
                    master.version += 1;
                    return true;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }


        }
        master.set = revised.set;
        master.version += 1;
        return true;
    }

    public boolean add(E obj) {

        VersionableSet<E> copySet = fork();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!copySet.set.add(obj)) { return false; }
        return merge(this, copySet, () -> { this.add(obj); });

    }

    public boolean add(VersionableFunction<E> function) {

        VersionableSet<E> copySet = fork();

        E obj = function.apply();

        if (!copySet.set.add(obj)) { return false; }
        return merge(this, copySet, () -> { this.add(obj); });

    }

    @Override
    public String toString() {
        return "VersionableSet { " +
                "set = " + set +
                ", version = " + version +
                " }";
    }
    public boolean remove(E obj) {

        VersionableSet<E> copySet = fork();

        if (!copySet.set.remove(obj)) { return false; }
        return merge(this, copySet, () -> { this.remove(obj); });
    }

    public boolean remove(VersionableFunction<E> function) {

        VersionableSet<E> copySet = fork();

        E obj = function.apply();

        if (!copySet.set.remove(obj)) { return false; }
        return merge(this, copySet, () -> { this.remove(obj); });
    }

}