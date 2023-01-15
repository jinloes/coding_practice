package com.jinloes.simple_functions;

import java.util.HashSet;
import java.util.Set;

/**
 * Given a number as a string, check if it is in a given base or not
 * <p>
 * func checkBase(num string, base int) bool {
 * //...
 * }
 * <p>
 * checkBase(“10110”, 2)
 * checkBase(“1234”, 10)
 * checkBase(“abcd1234”, 16)
 * checkBase(“01278”, 8)
 * checkBase(“ffff”, 16)
 * checkBase(“ffff”, 32)
 * checkBase(“ffff”, 12)
 * checkBase(“FFFF”, 16)
 * checkBase(“0xffff”, 16)
 */
public class CheckBase {

    public boolean checkBase(String str, int base) {
        if (str == null || str.isEmpty() || base > 36) {
            return false;
        }
        str = str.replaceAll("^0x", "");

        Set<Character> validChars = new HashSet<>();
        for (int i = 0; i < base; i++) {
            if (i <= 9) {
                validChars.add(Character.forDigit(i, 10));
            } else {
                // 10
                char c = (char) ((int) 'a' + (i - 10));
                validChars.add(c);
            }
        }


        for (Character c : str.toCharArray()) {
            Character lower = Character.toLowerCase(c);
            if (!validChars.contains(lower)) {
                return false;
            }
        }

        return true;
    }
}
