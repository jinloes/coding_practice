package com.jinloes.simple_functions;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreeSumTest {

    @Test
    public void testThreeSum() {
        int[] arr = new int[]{-1, 0, 1, 2, -1, -4};

        assertThat(ThreeSum.threeSum(arr))
                .contains(Lists.newArrayList(-1, -1, 2),
                        Lists.newArrayList(-1, 0, 1));
    }

}