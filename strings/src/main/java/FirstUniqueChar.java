import java.util.Arrays;

public class FirstUniqueChar {
    public static int firstUniqChar(String s) {
        int[] charArr = new int[26];
        Arrays.fill(charArr, -1);

        for (int i = 0; i < s.length(); i++) {
            Character c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                int idx = c - 'A';
                if (charArr[idx] == -1) {
                    charArr[idx] = i;
                } else {
                    charArr[idx] = -2;
                }
            } else {
                int idx = c - 'a';
                if (charArr[idx] == -1) {
                    charArr[idx] = i;
                } else {
                    charArr[idx] = -2;
                }
            }
        }

        int minIdx = Integer.MAX_VALUE;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] < minIdx && charArr[i] >= 0) {
                minIdx = charArr[i];
            }
        }

        return minIdx == Integer.MAX_VALUE ? -1 : minIdx;
    }
}
