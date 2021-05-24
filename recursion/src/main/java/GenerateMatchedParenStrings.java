import java.util.ArrayList;
import java.util.List;

/**
 * Write a program that takes as input a number and returns all the strings with that number of matched pairs of parens.
 */
public class GenerateMatchedParenStrings {
    public static List<String> generate(int numMatched) {
        List<String> result = new ArrayList<>();

        generate(numMatched, numMatched, "", result);

        return result;
    }

    private static void generate(int leftParensNeed, int rightParensNeeded, String current, List<String> result) {
        if (leftParensNeed == 0 && rightParensNeeded == 0) {
            result.add(current);
            return;
        }

        if (leftParensNeed > 0) {
            generate(leftParensNeed - 1, rightParensNeeded, current + "(", result);
        }

        if (rightParensNeeded > leftParensNeed) {
            generate(leftParensNeed, rightParensNeeded - 1, current + ")", result);
        }
    }

}
