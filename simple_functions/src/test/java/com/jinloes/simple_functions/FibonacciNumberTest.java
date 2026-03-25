package com.jinloes.simple_functions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FibonacciNumberTest {

    @Test
    void secondFibonacciNumber() {
        assertThat(FibonacciNumber.getNumber(2)).isEqualTo(1);
    }

    @Test
    void thirteenthFibonacciNumber() {
        assertThat(FibonacciNumber.getNumber(13)).isEqualTo(144);
    }
}