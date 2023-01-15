package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AllOneTest {
    private AllOne allOne;

    @BeforeEach
    void setUp() {
        allOne = new AllOne();
    }

    @Test
    public void test() {
        allOne.inc("a");
        allOne.inc("b");
        allOne.inc("b");
        allOne.inc("c");
        allOne.inc("c");
        allOne.inc("c");

        allOne.dec("b");
        allOne.dec("b");

        assertThat(allOne.getMinKey()).isEqualTo("a");

        allOne.dec("a");

        assertThat(allOne.getMaxKey()).isEqualTo("c");

        assertThat(allOne.getMinKey()).isEqualTo("c");
    }

    @Test
    public void test2() {
        allOne.inc("a");
        allOne.inc("b");
        allOne.inc("b");
        allOne.inc("b");
        allOne.inc("b");

        allOne.dec("b");
        allOne.dec("b");

        allOne.getMaxKey();
        allOne.getMinKey();

        allOne.getMaxKey();
        allOne.getMinKey();
    }
}