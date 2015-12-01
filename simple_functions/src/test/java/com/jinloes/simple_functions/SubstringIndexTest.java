package com.jinloes.simple_functions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Tests for {@link SubstringIndex}.
 */
public class SubstringIndexTest {
    @Test
    public void testFindSubstringIndex() {
        assertThat(SubstringIndex.findSubstringIndex("abc", "abc123")).isEqualTo(0);
        assertThat(SubstringIndex.findSubstringIndex("xyz", "abc123")).isEqualTo(-1);
        assertThat(SubstringIndex.findSubstringIndex("bc", "abc123")).isEqualTo(1);
        assertThat(SubstringIndex.findSubstringIndex("123456", "abc123")).isEqualTo(-1);
        assertThat(SubstringIndex.findSubstringIndex(null, null)).isEqualTo(-1);
        assertThat(SubstringIndex.findSubstringIndex(null, "abc123")).isEqualTo(-1);
        assertThat(SubstringIndex.findSubstringIndex("abc", null)).isEqualTo(-1);
        assertThat(SubstringIndex.findSubstringIndex("", "abc123")).isEqualTo(0);
        assertThat(SubstringIndex.findSubstringIndex("123", "abc123")).isEqualTo(3);
        assertThat(SubstringIndex.findSubstringIndex("c12", "abc123")).isEqualTo(2);
        assertThat(SubstringIndex.findSubstringIndex("abc", "abcabc")).isEqualTo(0);
    }
}
