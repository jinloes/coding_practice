package com.jinloes.simple_functions.string;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OneEditTest {
    private OneEdit oneEdit;

    @Before
    public void setUp() throws Exception {
        oneEdit = new OneEdit();
    }

    @Test
    public void isOneEdit() {
        assertThat(oneEdit.isOneEditAway("pale", "ple")).isTrue();
        assertThat(oneEdit.isOneEditAway("pales", "pale")).isTrue();
        assertThat(oneEdit.isOneEditAway("pale", "bale")).isTrue();
        assertThat(oneEdit.isOneEditAway("pale", "bake")).isFalse();
    }
}