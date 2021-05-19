/**
 * Implement a function for reversing the words in a string s.
 */
public class ReverseWords {
    public static String reverse(String text) {
        StringBuilder result = new StringBuilder();

        String[] parts = text.split(" ");

        for (int i = parts.length - 1; i >= 0; i--) {
            result.append(parts[i]);
            result.append(' ');
        }

        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

}
