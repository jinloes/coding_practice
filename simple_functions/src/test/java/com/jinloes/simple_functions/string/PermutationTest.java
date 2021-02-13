package com.jinloes.simple_functions.string;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PermutationTest {
    private Permutation permutation = new Permutation();

    @Before
    public void setUp() throws Exception {
        permutation = new Permutation();
    }

    @Test
    public void isPermutation() {
        assertThat(permutation.isPermutation("abc", "cab")).isTrue();
    }

    @Test
    public void isPermutationFalse() {
        assertThat(permutation.isPermutation("abc", "caddafdsfsad")).isFalse();
    }

    @Test
    public void isPermutationS1Null() {
        assertThat(permutation.isPermutation(null, "cad")).isFalse();
    }

    @Test
    public void isPermutationS2Null() {
        assertThat(permutation.isPermutation("cad", null)).isFalse();
    }

    @Test
    public void isPermutationNull() {
        assertThat(permutation.isPermutation(null, null)).isTrue();
    }
}