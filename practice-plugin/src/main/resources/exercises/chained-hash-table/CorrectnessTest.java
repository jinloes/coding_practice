package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void rejectsInvalidCapacityAndNullKeys() {
        assertThatThrownBy(() -> new Solution(0)).isInstanceOf(IllegalArgumentException.class);
        Solution table = new Solution(3);
        assertThatThrownBy(() -> table.put(null, "x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> table.get(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> table.containsKey(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> table.remove(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullValueIsStillAPresentKey() {
        Solution table = new Solution(2);
        table.put("nil", null);
        assertThat(table.get("nil")).isNull();
        assertThat(table.containsKey("nil")).isTrue();
        assertThat(table.size()).isEqualTo(1);
    }

    @Test
    void handlesManyCollisionsInOneBucket() {
        Solution table = new Solution(1);
        for (int i = 0; i < 50; i++) {
            table.put("k" + i, "v" + i);
        }
        for (int i = 0; i < 50; i++) {
            assertThat(table.get("k" + i)).as("key %d", i).isEqualTo("v" + i);
        }
        assertThat(table.size()).isEqualTo(50);
    }

    @Test
    void removalWorksAtHeadMiddleAndTailOfChain() {
        Solution table = new Solution(1);
        table.put("a", "1");
        table.put("b", "2");
        table.put("c", "3");
        assertThat(table.remove("b")).isTrue();
        assertThat(table.get("a")).isEqualTo("1");
        assertThat(table.get("b")).isNull();
        assertThat(table.get("c")).isEqualTo("3");
        assertThat(table.remove("c")).isTrue();
        assertThat(table.remove("a")).isTrue();
        assertThat(table.size()).isZero();
    }

    @Test
    void overwritesAndRemovesLargeSet() {
        Solution table = new Solution(17);
        for (int i = 0; i < 300; i++) {
            table.put("k" + i, "v" + i);
        }
        for (int i = 0; i < 300; i += 3) {
            table.put("k" + i, "changed" + i);
        }
        for (int i = 0; i < 300; i += 4) {
            assertThat(table.remove("k" + i)).isTrue();
        }
        for (int i = 0; i < 300; i++) {
            if (i % 4 == 0) {
                assertThat(table.containsKey("k" + i)).isFalse();
            } else if (i % 3 == 0) {
                assertThat(table.get("k" + i)).isEqualTo("changed" + i);
            } else {
                assertThat(table.get("k" + i)).isEqualTo("v" + i);
            }
        }
    }
}
