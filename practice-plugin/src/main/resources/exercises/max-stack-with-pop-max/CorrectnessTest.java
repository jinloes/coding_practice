package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void popMaxLeavesOtherValuesInStackOrder() {
        Solution stack = new Solution();
        for (int value : new int[]{3, 9, 1, 9, 2}) {
            stack.push(value);
        }
        assertThat(stack.popMax()).isEqualTo(9);
        assertThat(stack.pop()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(1);
        assertThat(stack.peekMax()).isEqualTo(9);
    }

    @Test
    void randomizedAgainstSimpleModel() {
        Solution stack = new Solution();
        List<Integer> model = new ArrayList<>();
        Random random = new Random(7);
        for (int i = 0; i < 200; i++) {
            if (model.isEmpty() || random.nextInt(3) == 0) {
                int value = random.nextInt(21) - 10;
                stack.push(value);
                model.add(value);
            } else if (random.nextBoolean()) {
                assertThat(stack.pop()).as("pop at step %s", i).isEqualTo(model.remove(model.size() - 1));
            } else {
                int max = model.stream().mapToInt(Integer::intValue).max().orElseThrow();
                int index = model.lastIndexOf(max);
                assertThat(stack.popMax()).as("popMax at step %s", i).isEqualTo(model.remove(index));
            }
            if (!model.isEmpty()) {
                assertThat(stack.top()).as("top at step %s", i).isEqualTo(model.get(model.size() - 1));
                assertThat(stack.peekMax()).as("max at step %s", i).isEqualTo(model.stream().mapToInt(Integer::intValue).max().orElseThrow());
            }
        }
    }
}
