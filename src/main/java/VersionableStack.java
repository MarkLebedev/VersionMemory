import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class VersionableStack<E> {

    private Stack<E> stack;
    private Integer version;

    private final ResolveStrategy strategy;

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
    @SuppressWarnings("unchecked")
    private VersionableStack<E> fork() {
        Stack<E> newStack = (Stack<E>)stack.clone();
        return new VersionableStack<E>(newStack, version + 1, strategy);
    }

    private void merge(VersionableStack<E> master,
                       VersionableStack<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.version - 1, master.version)) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

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

        copyStack.stack.add(obj);

        merge(this, copyStack, () -> { this.add(obj); });

    }

    public void add(VersionableFunction<E> function) {

        VersionableStack<E> copyStack = fork();

        E obj = function.apply();

        copyStack.stack.add(obj);

        merge(this, copyStack, () -> { this.add(obj); });

    }

    public E pop() {

        VersionableStack<E> copyStack = fork();

        var object = copyStack.stack.pop();

        merge(this, copyStack, this::pop);

        return object;
    }

    @Override
    public String toString() {
        return "VersionableStack{ " +
                "stack = " + stack +
                ", version = " + version +
                " }";
    }

}