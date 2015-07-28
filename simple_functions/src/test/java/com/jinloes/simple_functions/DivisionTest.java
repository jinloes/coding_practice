package com.jinloes.simple_functions;

import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Division}.
 */
public class DivisionTest {
    @Test
    public void testDivide() {
        assertThat(Division.divide(5, 3, 3)).isEqualTo("1.667");
        assertThat(Division.divide(5, 2, 2)).isEqualTo("2.50");
        assertThat(Division.divide(0, 2, 5)).isEqualTo("0.00000");
        assertThat(Division.divide(0, 0, 2)).isEqualTo(Objects.toString(Float.NaN));
    }
}