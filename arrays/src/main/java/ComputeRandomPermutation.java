import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Design an algorithm that creates uniformly random permutations of {0,1... n-1). You are givena random number generator
 * that returns integers in the set {0,1,...,n-1) with equal probability; use as few calls to it as possible.
 */
public class ComputeRandomPermutation {
    public List<Integer> compute(int n) {
        List<Integer> permutation = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            permutation.add(i);
        }

        Random random = new Random();

        for (int i = 0; i < permutation.size(); i++) {
            Collections.swap(permutation, i, i + random.nextInt(permutation.size() - i));
        }

        return permutation;
    }
}
