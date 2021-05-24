import java.util.ArrayList;
import java.util.List;

/**
 * Write a function that takes as input a set and returns its power set.
 */
public class GeneratePowerSet {
    public static List<List<Integer>> generate(List<Integer> toGenerateFrom) {
        List<List<Integer>> result = new ArrayList<>();

        generate(0, toGenerateFrom, new ArrayList<>(), result);

        return result;
    }

    private static void generate(int candidateIdx, List<Integer> toGenerateFrom, List<Integer> current, List<List<Integer>> result) {
        if (candidateIdx == toGenerateFrom.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        current.add(toGenerateFrom.get(candidateIdx));
        generate(candidateIdx + 1, toGenerateFrom, current, result);
        current.remove(current.size() - 1);
        generate(candidateIdx + 1, toGenerateFrom, current, result);


    }
}
