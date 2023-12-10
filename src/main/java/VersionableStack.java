import java.util.Objects;
import java.util.Stack;

public class VersionableStack<E> {

    private Stack<E> stack;
    private Integer version;

    private ResolveStrategy strategy;

    public VersionableStack(ResolveStrategy strategy) {
        this.stack = new Stack<E>();
        this.strategy = strategy;
        this.version = 0;
    }

    private VersionableStack(Stack<E> s, Integer version, ResolveStrategy strategy) {
        this.stack = s;
        this.version = version;
        this.strategy = strategy;
    }
    private VersionableStack<E> fork() {
        Stack<E> newStack = (Stack<E>)stack.clone();
        return new VersionableStack<E>(newStack, version + 1, strategy);
    }

    private void merge(VersionableStack<E> master,
                       VersionableStack<E> revised, Runnable change) {
        System.out.println("Merge: master = " + master.toString() + " revised = " + revised.toString());

        if (!Objects.equals(revised.version - 1, master.version)) {
            System.out.println(String.format("Conflict: Master - %s, Revised - %s", master.toString(), revised.toString()));

            switch (strategy) {

                case LEFT -> { return; }
                case RIGHT -> {
                    master.stack = revised.stack;
                    master.version += 1;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }

            return;
        }
        master.stack = revised.stack;
        master.version += 1;
    }

    public void add(E obj) {

        VersionableStack<E> copyStack = fork();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        copyStack.stack.add(obj);

        merge(this, copyStack, () -> { this.add(obj); });

    }

    @Override
    public String toString() {
        return "VersionableStack{" +
                "stack=" + stack +
                ", version=" + version +
                '}';
    }
    public E pop() {

        VersionableStack<E> copyStack = fork();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var object = copyStack.stack.pop();

        merge(this, copyStack, this::pop);

        return object;
    }
//
}