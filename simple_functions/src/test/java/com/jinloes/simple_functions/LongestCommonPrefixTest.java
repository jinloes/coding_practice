package com.jinloes.simple_functions;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestCommonPrefixTest {


    @Test
    public void testLongestCommonPrefix() {
        LongestCommonPrefix longestCommonPrefix = new LongestCommonPrefix();


        assertThat(longestCommonPrefix.longestCommonPrefix(new String[]{"a", "ab"}))
                .isEqualTo("a");
    }

}