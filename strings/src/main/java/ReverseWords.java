/**
 * Write a function that reverses the order of the words in a string.
 * Assume that all words are space delimited and treat punctuation the same as letters.
 */
public class ReverseWords {

    public static String reverse(String str) {
        String[] parts = str.split(" ");

        int start = 0;
        int end = parts.length - 1;

        for (int i = 0; i < parts.length / 2; i++) {
            String temp = parts[start + i];
            parts[start + i] = parts[end - i];
            parts[end - i] = temp;
        }

        return String.join(" ", parts);
    }

}
