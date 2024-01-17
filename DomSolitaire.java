public class DomSolitaire {

    private StackADT<Domino>[] foundation;
    private StackADT<Domino>[] stack;
    private String name;
    private boolean debug;

    public DomSolitaire(int highestNum, int seed, String name) {
        this.name = name;
        foundation = new StackADT[highestNum + 1];
        stack = new StackADT[highestNum + 1];
        for (int i = 0; i <= highestNum; i++) {
            foundation[i] = new StackLL<>("F" + i);
            stack[i] = new StackLL<>("S" + i);
        }

        Domino[] set = Domino.getSet(highestNum);
        Domino.shuffle(set, seed);

        int k = 0;
        for (int i = 0; i < stack.length; i++) {
            for (int j = 0; j <= i; j++) {
                stack[i].push(set[k++]);
            }
        }
//        stack[0].push(set[0]);
//        stack[1].push(set[5]);
//        stack[1].push(set[4]);
//        stack[2].push(set[2]);
//        stack[2].push(set[3]);
//        stack[2].push(set[1]);
    }

    public String getName() {
        return name;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void reset(int seed) {

        Domino[] set = Domino.getSet(foundation.length - 1);
        Domino.shuffle(set, seed);

        for (int i = 0; i < foundation.length; i++) {
            foundation[i] = new StackLL<>("F" + i);
            stack[i] = new StackLL<>("S" + i);
        }

        int k = 0;
        for (int i = 0; i < stack.length; i++) {

            for (int j = 0; j <= i; j++) {
                stack[i].push(set[k++]);
            }
        }

    }

    public boolean winner() {
        for (StackADT<Domino> s : stack) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public UnorderedListADT<Move> findSFMoves() {

        UnorderedListADT<Move> list = new UnorderedList();
        for (StackADT<Domino> s : stack) {
            if (!s.isEmpty()) {
                Domino from = s.peek();

                for (int i = 0; i < foundation.length; i++) {
                    StackADT<Domino> f = foundation[i];
                    if (f.isEmpty()
                            && from.getHigh() == i
                            && from.getLow() == i) {
                        Move move = new Move(s, f);
                        list.addToFront(move);
                    } else if (!f.isEmpty() && f.size() <= i && from.isOneDown(f.peek())) {
                        Move move = new Move(s, f);
                        list.addToFront(move);
                    }
                }
            }

        }

        return list;
    }

    public UnorderedListADT<Move> findSESMoves() {
        UnorderedListADT<Move> list = new UnorderedList();
        for (int i = 0; i < stack.length; i++) {
            if (!stack[i].isEmpty()) {
                for (int j = 0; j < stack.length; j++) {
                    if (stack[j].isEmpty() && stack[i].peek().getLow() == 0) {
                        Move move = new Move(stack[i], stack[j]);
                        list.addToFront(move);
                    }
                }
            }

        }
        return list;
    }

    public UnorderedListADT<Move> findSSMoves() {
        UnorderedListADT<Move> list = new UnorderedList();
        for (int i = 0; i < stack.length; i++) {
            if (!stack[i].isEmpty()) {
                for (int j = 0; j < stack.length; j++) {
                    if (!stack[j].isEmpty() && stack[i].peek().isOneUp(stack[j].peek())) {
                        Move move = new Move(stack[i], stack[j]);
                        list.addToFront(move);
                    }
                }
            }

        }
        return list;
    }

    public void findMoves(StackADT<Move> st) {
        UnorderedList<Move> SESMoves = (UnorderedList<Move>) findSESMoves();
        while (!SESMoves.isEmpty()) {
            st.push(SESMoves.removeFirst());
        }
        UnorderedList<Move> SSMoves = (UnorderedList<Move>) findSSMoves();
        while (!SSMoves.isEmpty()) {
            st.push(SSMoves.removeFirst());
        }
        UnorderedList<Move> SFMoves = (UnorderedList<Move>) findSFMoves();
        while (!SFMoves.isEmpty()) {
            st.push(SFMoves.removeFirst());
        }
    }

    public Move createMove(String from, String to) {

        StackADT<Domino> fromDomino = getDomino(from);
        StackADT<Domino> toDomino = getDomino(to);

        Move move = new Move(fromDomino, toDomino);
        return move;
    }

    private StackADT<Domino> getDomino(String string) {
        if (string.charAt(0) == 'F') {
            int index = Integer.parseInt(string.charAt(1) + "");
            return foundation[index];
        }
        if (string.charAt(0) == 'S') {
            int index = Integer.parseInt(string.charAt(1) + "");
            return stack[index];
        }
        return null;

    }

    @Override
    public String toString() {
        String data = "";
        int i = 1;
        for (StackADT<Domino> f : foundation) {
            data += ((StackLL) f).getName() + " " + f.size() + "/" + i + "  ";
            i++;
        }
        for (StackADT<Domino> stackADT : stack) {
            data += "\n" + stackADT.toString();
        }
        return name + " " + data;
    }

    public String showNamedContent() {
        String data = "";
        for (StackADT<Domino> stackADT : foundation) {
            data += stackADT.size();
        }
        data += "|";
        for (StackADT<Domino> stackADT : stack) {
            data += ((StackLL) stackADT).showNamedContent();
        }
        return data;
    }

    public StackADT<Move> findSolution(int maxSteps) {
        StackADT<Move> moves = new StackLL<>();
        int countSteps = 0;

        findMoves(moves);
        
        StackLL<Move> solution = new StackLL<>();

        while (!moves.isEmpty() && !winner()) {
            Move top = moves.peek();
            if (!top.isCompleted()) {
                try {
                    top.doIt();
                    if (countSteps < maxSteps) {
                        if (winner()) {
                            break;
                        }
                        findMoves(moves);
                    } else {

                        top.undoIt();
                        return null;
                    }
                    countSteps++;

                } catch (Exception e) {
                    moves.pop();
                }
            } else {
                top.undoIt();
                moves.pop();
                countSteps--;
            }

        }
        while (!moves.isEmpty()) {
            Move pop = moves.pop();
            if (pop.isCompleted()) {
                solution.push(pop);
            }
        }

        if (countSteps > maxSteps) {
            return null;
        }

        return solution;
    }

    public static void main(String[] args) {
        DomSolitaire ds = new DomSolitaire(2, 1998, "max");
        System.out.println(ds.toString());

        System.out.println(ds.findSFMoves());
        System.out.println(ds.findSSMoves());
        System.out.println(ds.findSESMoves());

        StackLL s = new StackLL();
        ds.findMoves(s);
        System.out.println("Find Moves: " + s.toString());

        String result = ds.findSolution(1000) + "";
        System.out.println("Result-> " + result);
    }

}