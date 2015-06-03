package com.jinloes.simple_functions;

import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link Division}.
 */
public class DivisionTest {
    @Test
    public void testDivide() {
        assertThat(Division.divide(5, 3, 3), is("1.667"));
        assertThat(Division.divide(5, 2, 2), is("2.50"));
        assertThat(Division.divide(0, 2, 5), is("0.00000"));
        assertThat(Division.divide(0, 0, 2), is(Objects.toString(Float.NaN)));
    }
}