import java.util.*;

public class VersionableSet<E> {

    private HashSet<E> set;
    private Integer masterState;
    private Integer state() { return set.hashCode(); }

    private final ResolveStrategy strategy;

    public VersionableSet(ResolveStrategy strategy) {
        this.set = new HashSet<E>();
        this.strategy = strategy;
        this.masterState = set.hashCode();
    }

    private VersionableSet(HashSet<E> s, Integer masterState, ResolveStrategy strategy) {
        this.set = s;
        this.masterState = masterState;
        this.strategy = strategy;
    }
    @SuppressWarnings("unchecked")
    private VersionableSet<E> fork() {
        HashSet<E> newSet = (HashSet<E>)set.clone();
        return new VersionableSet<E>(newSet, this.state(), strategy);
    }

    private synchronized boolean merge(VersionableSet<E> master,
                       VersionableSet<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.masterState, master.state())) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return false; }
                case RIGHT -> {
                    master.set = revised.set;
                    return true;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }


        }
        master.set = revised.set;
        return true;
    }

    public boolean add(E obj) {

        VersionableSet<E> copySet = fork();

        if (!copySet.set.add(obj)) { return false; }
        return merge(this, copySet, () -> { this.add(obj); });

    }

    public boolean addAll(List<E> collection) {

        VersionableSet<E> copySet = fork();

        if (!copySet.set.addAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.addAll(collection); });

    }

    public boolean add(VersionableFunction<E> function) {

        VersionableSet<E> copySet = fork();

        E obj = function.apply();

        if (!copySet.set.add(obj)) { return false; }
        return merge(this, copySet, () -> { this.add(obj); });

    }

    public boolean addAll(Collection<VersionableFunction<E>> functionCollection) {

        VersionableSet<E> copySet = fork();

        List<E> collection = functionCollection.stream().map( VersionableFunction::apply ).toList();

        if (!copySet.set.addAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.addAll(collection); });
    }

    @Override
    public String toString() {
        return "VersionableSet { " +
                "set = " + set +
                " masterState = " + masterState +
                " state = " + state() +
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

    public boolean removeAll(List<E> collection) {

        VersionableSet<E> copySet = fork();

        if (!copySet.set.removeAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.removeAll(collection); });

    }

    public boolean removeAll(Collection<VersionableFunction<E>> functionCollection) {

        VersionableSet<E> copySet = fork();

        List<E> collection = functionCollection.stream().map( VersionableFunction::apply ).toList();

        if (!copySet.set.removeAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.removeAll(collection); });
    }


    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(E object) {

        VersionableSet<E> copySet = fork();

        copySet.set.contains(object);

        return merge(this, copySet, () -> { this.contains(object); });
    }

    public boolean containsAll(Collection<E> collection) {

        VersionableSet<E> copySet = fork();

        copySet.set.containsAll(collection);

        return merge(this, copySet, () -> { this.containsAll(collection); });
    }

    public void clear() {

        VersionableSet<E> copySet = fork();

        copySet.set.clear();

        merge(this, copySet, () -> { this.clear(); });
    }

    public boolean retainAll(List<E> collection) {

        VersionableSet<E> copySet = fork();

        if (!copySet.set.retainAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.retainAll(collection); });
    }

    public boolean retainAll(Collection<VersionableFunction<E>> functionCollection) {

        VersionableSet<E> copySet = fork();

        List<E> collection = functionCollection.stream().map( VersionableFunction::apply ).toList();

        if (!copySet.set.retainAll(collection)) { return false; }
        return merge(this, copySet, () -> { this.retainAll(collection); });
    }

}
