package com.jinloes.practice;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNegativeAndAlreadyGroupedKeys() {
        int[] keys={0,-1,2,-1,0,2,-1}; Solution.groupEqualKeys(keys); assertGrouped(keys,0,-1,2,-1,0,2,-1);
        keys = new int[]{9,9,1,1,1,4}; Solution.groupEqualKeys(keys); assertGrouped(keys,9,9,1,1,1,4);
    }
    @Test
    void groupsLargeSeededInputAndPreservesLength() {
        int[] keys = new int[1000]; for(int i=0;i<keys.length;i++) keys[i]=(i*37)%17; int[] original=keys.clone(); Solution.groupEqualKeys(keys); assertGrouped(keys, original);
        keys = new int[]{4,3,4,3,4,2}; Solution.groupEqualKeys(keys); assertThat(keys).hasSize(6); assertGrouped(keys,4,3,4,3,4,2);
    }

    private static void assertGrouped(int[] keys, int... original) {
        assertThat(keys).containsExactlyInAnyOrder(original);
        for (int i = 0; i < keys.length; i++) for (int j = i + 1; j < keys.length; j++) if (keys[i] == keys[j]) for (int k = i; k <= j; k++) assertThat(keys[k]).as("block from %d to %d", i, j).isEqualTo(keys[i]);
    }

}

