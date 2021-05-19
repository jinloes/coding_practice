/**
 * Given two strings s (the "search string") and t (the "text"), find the first occurrence of s in t.
 */
public class FindSubstring {

    public static int find(String toSearch, String toFind) {
        int left = 0;
        int right = 0;
        int toFindIdx = 0;

        while (right < toSearch.length()) {
            if (toSearch.charAt(right) == toFind.charAt(toFindIdx)) {
                right++;
                toFindIdx++;
                if (toFindIdx >= toFind.length()) {
                    return left;
                }
            } else {
                left = right + 1;
                right = left;
                toFindIdx = 0;
            }
        }

        return -1;
    }
}
