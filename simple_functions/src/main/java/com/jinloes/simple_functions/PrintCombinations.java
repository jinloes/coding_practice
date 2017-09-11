package com.jinloes.simple_functions;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Print all the combinations of a string replacing ? with 0 or 1
 * ex 1? -> 10, 11
 */
public class PrintCombinations {

    public static List<String> compute(String str) {
        if(str.indexOf('?') == -1) {
            return Lists.newArrayList(str);
        }
        String zeroStr = str.replaceFirst("\\?", "0");
        String oneStr = str.replaceFirst("\\?", "1");
        List<String> zeroCominbations = compute(zeroStr);
        List<String> oneCombinations = compute(oneStr);
        return FluentIterable.from(zeroCominbations).append(oneCombinations).toList();
    }
}
