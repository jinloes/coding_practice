package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void groupsRepeatedKeys() { int[] keys={3,1,3,2,1}; Solution.groupEqualKeys(keys); assertGrouped(keys,3,1,3,2,1); }
    @Test
    void keepsSingleExistingBlockValid() { int[] keys={5,5,5}; Solution.groupEqualKeys(keys); assertGrouped(keys,5,5,5); }
    @Test
    void handlesEmptyInput() { int[] keys={}; Solution.groupEqualKeys(keys); assertThat(keys).isEmpty(); }

    private static void assertGrouped(int[] keys, int... original) {
        assertThat(keys).containsExactlyInAnyOrder(original);
        for (int i = 0; i < keys.length; i++) for (int j = i + 1; j < keys.length; j++) if (keys[i] == keys[j]) for (int k = i; k <= j; k++) assertThat(keys[k]).as("block from %d to %d", i, j).isEqualTo(keys[i]);
    }

}

