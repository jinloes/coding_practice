public class LongestCommonPrefix {
    public static String compute(String[] strs) {
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            String current = strs[i];
            prefix = computePrefix(prefix, current);
        }
        return prefix;
    }

    private static String computePrefix(String prefix, String str) {
        int length = Math.min(prefix.length(), str.length());
        for (int i = 0; i < length; i++) {
            if (prefix.charAt(i) != str.charAt(i)) {
                return prefix.substring(0, i);
            }
        }
        return prefix.length() < str.length() ? prefix : str;
    }
}
