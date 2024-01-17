
import java.util.Objects;

public class Move {

    private StackADT<Domino> from;
    private StackADT<Domino> to;
    private boolean completed;
    private String name;

    public Move(StackADT<Domino> from, StackADT<Domino> to) {
        this(from, to, "m");
    }

    public Move(StackADT<Domino> from, StackADT<Domino> to, String name) {
        this.from = from;
        this.to = to;
        this.name = name;
        this.completed = false;
    }

    public void doIt() {
        to.push(from.pop());
        completed = true;
    }

    public void undoIt() {
        from.push(to.pop());
        completed = false;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String fromS = this.from.toString();
        String toS = this.to.toString();
        if (from instanceof StackLL) {
            fromS = ((StackLL) from).getName();
        }
        if (to instanceof StackLL) {
            toS = ((StackLL) to).getName();
        }
        return name + fromS + "->" + toS + (completed ? "!" : "?");
    }

    
    
}