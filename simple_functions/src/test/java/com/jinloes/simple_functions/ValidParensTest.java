package com.jinloes.simple_functions;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidParensTest {

    private ValidParens validParens;

    @Before
    public void setUp() throws Exception {
        validParens = new ValidParens();
    }

    @Test
    public void isValid() {
        assertThat(validParens.isValid("(){}"))
                .isTrue();

        assertThat(validParens.isValid("{}{}()[]"))
                .isTrue();

        assertThat(validParens.isValid("(([]){})"))
                .isTrue();
    }
}