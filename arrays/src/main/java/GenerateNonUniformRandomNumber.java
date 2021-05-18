import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * Given a random number generator that produces values in [0,1] uniformly, how would you generate one of the n
 * numbers according to the specified probabilities?
 */
public class GenerateNonUniformRandomNumber {

    public static int generate(List<Integer> values, List<Double> probabilities) {
        TreeMap<Double, Integer> map = new TreeMap<>();

        for (int i = 0; i < values.size(); i++) {
            map.put(probabilities.get(i), values.get(i));
        }

        Random random = new Random();

        double val = random.nextDouble();

        return map.ceilingEntry(val).getValue();
    }
}
