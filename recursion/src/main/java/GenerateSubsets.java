import java.util.ArrayList;
import java.util.List;

/**
 * Write a program which computes all size k subsets of {1, 2,..., n}, where k and n are program inputs.
 */
public class GenerateSubsets {
    public static List<List<Integer>> generate(int n, int k) {
        List<Integer> candidates = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            candidates.add(i);
        }
        List<List<Integer>> results = new ArrayList<>();

        generate(0, candidates, k, new ArrayList<>(), results);

        return results;
    }

    private static void generate(int candidateIdx, List<Integer> candidates, int k, List<Integer> current, List<List<Integer>> results) {
        if (current.size() == k) {
            results.add(new ArrayList<>(current));
            return;
        }

        for (int i = candidateIdx; i < candidates.size(); i++) {
            current.add(candidates.get(i));
            generate(i + 1, candidates, k, current, results);
            current.remove(current.size() - 1);
        }
    }
}
