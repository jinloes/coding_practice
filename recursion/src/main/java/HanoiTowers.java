import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Write a program which prints a sequence of operations that transfers n rings from one peg to another.
 * You have a third peg, which is initially empty. The only operation you can perform is taking a single ring from
 * the top of one peg and placing it on the top of another peg.
 * <p>
 * You must never place a larger ring above a smaller ring.
 */
public class HanoiTowers {
    private static final int NUM_PEGS = 3;

    public static void computeTowerHanoi(int numRings) {
        List<Stack<Integer>> pegs = new ArrayList<>();

        for (int i = 0; i < NUM_PEGS; i++) {
            pegs.add(new Stack<>());
        }

        for (int i = numRings; i >= 1; i--) {
            pegs.get(0).add(i);
        }

        compute(numRings, pegs, 0, 1, 2);

        System.out.println(pegs.get(1));
    }

    private static void compute(int ringsToMove, List<Stack<Integer>> pegs, int from, int to, int toUse) {
        if (ringsToMove == 0) {
            return;
        }

        compute(ringsToMove - 1, pegs, from, toUse, to);
        pegs.get(to).add(pegs.get(from).pop());
        System.out.println(String.format("Moving peg from: %s to peg %s", from, to));
        compute(ringsToMove - 1, pegs, toUse, to, from);
    }
}
