package com.jinloes.coding_practice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MaxStack2Test {
    private MaxStack2 maxStack2;

    @BeforeEach
    void setUp() {
        maxStack2 = new MaxStack2();
    }

    @Test
    void test() {
        maxStack2.push(5);
        maxStack2.push(1);
        maxStack2.push(5);

        assertThat(maxStack2.top())
                .isEqualTo(5);
        assertThat(maxStack2.popMax())
                .isEqualTo(5);
        assertThat(maxStack2.top())
                .isEqualTo(1);
        assertThat(maxStack2.peekMax())
                .isEqualTo(5);
        assertThat(maxStack2.pop())
                .isEqualTo(1);
        assertThat(maxStack2.top())
                .isEqualTo(5);
    }
}