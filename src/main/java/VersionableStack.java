import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class VersionableStack<E> {

    private Stack<E> stack;

    private Integer masterState;
    private Integer state() { return stack.hashCode(); }

    private final ResolveStrategy strategy;

    public VersionableStack(ResolveStrategy strategy) {
        this.stack = new Stack<E>();
        this.strategy = strategy;
        this.masterState = stack.hashCode();
    }

    private VersionableStack(Stack<E> s, Integer masterState, ResolveStrategy strategy) {
        this.stack = s;
        this.masterState = masterState;
        this.strategy = strategy;
    }
    @SuppressWarnings("unchecked")
    private VersionableStack<E> fork() {
        Stack<E> newStack = (Stack<E>)stack.clone();
        return new VersionableStack<E>(newStack, this.state(), strategy);
    }

    private synchronized void merge(VersionableStack<E> master,
                       VersionableStack<E> revised, Runnable change) {
        System.out.printf("Merge: Master - %s, revised - %s%n", master.toString(), revised.toString());

        if (!Objects.equals(revised.masterState, master.state())) {
            System.out.printf("Conflict: Master - %s, Revised - %s%n", master.toString(), revised.toString());

            switch (strategy) {

                case LEFT -> { return; }
                case RIGHT -> {
                    master.stack = revised.stack;
                }
                case LEFT_RIGHT -> {
                    change.run();
                }
            }

            return;
        }
        master.stack = revised.stack;
    }

    public E push(E obj) {

        VersionableStack<E> copyStack = fork();

        copyStack.stack.add(obj);

        merge(this, copyStack, () -> { this.push(obj); });

        return obj;

    }

    public E push(VersionableFunction<E> function) {

        VersionableStack<E> copyStack = fork();

        E obj = function.apply();

        copyStack.stack.add(obj);

        merge(this, copyStack, () -> { this.push(obj); });

        return function.apply();

    }

    public E pop() {

        VersionableStack<E> copyStack = fork();

        var object = copyStack.stack.pop();

        merge(this, copyStack, this::pop);

        return object;
    }

    public E peek(){
        return this.stack.peek();
    }

    public boolean empty(){
        return this.stack.empty();
    }

    public int search(Object o){
        VersionableStack<E> copyStack = fork();

        var position = copyStack.stack.search(o);

        merge(this, copyStack, () -> {search(o);});

        return position;
    }

    @Override
    public String toString() {
        return "VersionableStack{ " +
                "stack = " + stack +
                " masterState = " + masterState +
                " state = " + state() +
                " }";
    }

}