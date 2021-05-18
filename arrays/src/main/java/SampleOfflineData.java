import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implement an algorithm that takes as input an array of distinct elements and a size, and returns a subset of the
 * given size of the array elements. All subsets should be equally likely.
 * Return the result in input array itself.
 */
public class SampleOfflineData {

    public static void sample(List<Integer> input, int subsetSize) {
        Random random = new Random();

        for (int i = 0; i < subsetSize; i++) {
            Collections.swap(input, i, i + random.nextInt(input.size() - i));
        }
    }
}
