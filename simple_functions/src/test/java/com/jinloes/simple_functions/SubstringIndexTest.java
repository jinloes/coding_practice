package com.jinloes.simple_functions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link SubstringIndex}.
 */
public class SubstringIndexTest {
    @Test
    public void testFindSubstringIndex() {
        assertThat(SubstringIndex.findSubstringIndex("abc", "abc123"), is(0));
        assertThat(SubstringIndex.findSubstringIndex("xyz", "abc123"), is(-1));
        assertThat(SubstringIndex.findSubstringIndex("bc", "abc123"), is(1));
        assertThat(SubstringIndex.findSubstringIndex("123456", "abc123"), is(-1));
        assertThat(SubstringIndex.findSubstringIndex(null, null), is(-1));
        assertThat(SubstringIndex.findSubstringIndex(null, "abc123"), is(-1));
        assertThat(SubstringIndex.findSubstringIndex("abc", null), is(-1));
        assertThat(SubstringIndex.findSubstringIndex("", "abc123"), is(0));
        assertThat(SubstringIndex.findSubstringIndex("123", "abc123"), is(3));
        assertThat(SubstringIndex.findSubstringIndex("c12", "abc123"), is(2));
        assertThat(SubstringIndex.findSubstringIndex("abc", "abcabc"), is(0));
    }
}
