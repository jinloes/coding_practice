import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Write a program that takes as input a permutation, and returns the next permutation under dictionary ordering.
 * If the permutation is the last permutation, return the empty array.
 * <p>
 * For example, if the input is (1,0,3,2) your function should return (1, 2, 0, 3). If the input is (3, 2,1, 0), return ().
 */
public class NextPermutation {

    public static int computeBruteForce(List<Integer> number) {
        Set<Integer> permutations = new HashSet<>();

        Set<Integer> candidates = new HashSet<>(number);

        // O(n * n!)
        getPermutations(candidates, permutations, 0);

        int numAsInt = 0;

        for (Integer digit : number) {
            numAsInt = numAsInt * 10 + digit;
        }

        int nextPermutation = 0;
        int minDiff = Integer.MAX_VALUE;

        for (Integer permutation : permutations) {
            int diff = permutation - numAsInt;
            if (diff > 0 && minDiff > diff) {
                minDiff = diff;
                nextPermutation = permutation;
            }
        }

        return nextPermutation;
    }

    private static void getPermutations(Set<Integer> candidates, Set<Integer> permutations, int currentVal) {
        if (candidates.isEmpty()) {
            permutations.add(currentVal);
            return;
        }

        for (Integer candidate : candidates) {
            Set<Integer> nextCandidates = new HashSet<>(candidates);
            nextCandidates.remove(candidate);

            getPermutations(nextCandidates, permutations, currentVal * 10 + candidate);
        }
    }


    public static int compute(List<Integer> digits) {
        int permuteIdx = digits.size() - 2;

        while (permuteIdx >= 0 && digits.get(permuteIdx) >= digits.get(permuteIdx + 1)) {
            permuteIdx--;
        }

        // max number in permutation, can't be any larger
        if (permuteIdx == -1) {
            return 0;
        }

        for (int i = digits.size() - 1; i > permuteIdx; i--) {
            if (digits.get(i) > digits.get(permuteIdx)) {
                Collections.swap(digits, i, permuteIdx);
                break;
            }
        }

        Collections.reverse(digits.subList(permuteIdx + 1, digits.size()));

        int result = 0;

        for (Integer digit : digits) {
            result = result * 10 + digit;
        }

        return result;
    }

    public static int nextSmaller(List<Integer> digits) {
        int permuteIdx = digits.size() - 2;

        while (permuteIdx >= 0 && digits.get(permuteIdx) <= digits.get(permuteIdx + 1)) {
            permuteIdx--;
        }

        if (permuteIdx == -1) {
            return 0;
        }

        for (int i = digits.size() - 1; i > permuteIdx; i--) {
            if (digits.get(i) < digits.get(permuteIdx)) {
                Collections.swap(digits, i, permuteIdx);
                break;
            }
        }

        Collections.reverse(digits.subList(permuteIdx + 1, digits.size()));

        int result = 0;

        for (Integer digit : digits) {
            result = result * 10 + digit;
        }

        return result;
    }


}
