import java.util.HashSet;
import java.util.Set;

/**
 * Write a program that determines where to add periods to a decimal string so that the resulting string is a
 * valid IP address. There may be more than one valid IP address corresponding to a string, in which case you
 * should print all possibilities.
 */
public class ComputeIpAddress {
    public static Set<String> compute(String str) {
        Set<String> ipAddresses = new HashSet<>();

        compute(str, 0, 0, new StringBuilder(), ipAddresses);

        return ipAddresses;
    }

    private static void compute(String str, int idx, int numParts, StringBuilder current, Set<String> ipAddresses) {
        if (idx >= str.length() && numParts == 4) {
            current.deleteCharAt(current.length() - 1);
            ipAddresses.add(current.toString());
            return;
        } else if (idx >= str.length() || numParts > 4) {
            return;
        }


        String currentPart = "";

        for (int i = 0; i < 3 && i + idx < str.length(); i++) {
            currentPart += str.charAt(idx + i);

            if (isValid(currentPart)) {
                current.append(currentPart);
                current.append(".");
                compute(str, idx + i + 1, numParts + 1, current, ipAddresses);
                // remove last combo
                int len = currentPart.length();
                if (current.charAt(current.length() - 1) == '.') {
                    len++;
                }
                current.setLength(current.length() - len);
            }

        }
    }

    private static boolean isValid(String str) {
        if (str.startsWith("0") && str.length() > 1) {
            return false;
        }
        int num = Integer.parseInt(str);

        return num <= 255 && num >= 0 && str.length() < 4;
    }
}
