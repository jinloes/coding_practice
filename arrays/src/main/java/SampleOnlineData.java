import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Design a program that takes as input a size k, and reads packets, continuously maintaining a uniform random
 * subset of size k of the read packets.
 */
public class SampleOnlineData {
    public static List<Integer> sample(Iterator<Integer> sequence, int k) {
        Random random = new Random();

        List<Integer> subset = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            if (sequence.hasNext()) {
                subset.add(sequence.next());
            }
        }

        while (sequence.hasNext()) {
            int idx = random.nextInt(k);

            subset.set(idx, sequence.next());
        }

        return subset;
    }
}
