import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Write a program which takes as input an array of distinct integers and generates all permutations of that array.
 * No permutation of the array may appear more than once.
 */
public class GeneratePermutations {
    public static List<List<Integer>> permutations(List<Integer> candidates) {
        candidates.sort(Comparator.naturalOrder());
        List<List<Integer>> result = new ArrayList<>();

        permutations(0, candidates, result);

        return result;
    }

    private static void permutations(int candidateIdx, List<Integer> candidates, List<List<Integer>> result) {
        if (candidateIdx == candidates.size() - 1) {
            result.add(new ArrayList<>(candidates));
        }

        for (int i = candidateIdx; i < candidates.size(); i++) {
            if (shouldSwap(candidates, candidateIdx)) {
                Collections.swap(candidates, candidateIdx, i);
                permutations(candidateIdx + 1, candidates, result);
                Collections.swap(candidates, candidateIdx, i);
            }
        }
    }

    private static boolean shouldSwap(List<Integer> candidates, int idx) {
        if (idx == 0) {
            return true;
        }

        return !candidates.get(idx).equals(candidates.get(idx - 1));
    }
}
