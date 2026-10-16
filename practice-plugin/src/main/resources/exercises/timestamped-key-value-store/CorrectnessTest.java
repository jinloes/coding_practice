package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void keysAreIndependent() {
        Solution store = new Solution();
        store.set("a", "one", 10);
        store.set("b", "two", 3);
        assertThat(store.get("a", 5)).as("get(a, 5)").isEmpty();
        assertThat(store.get("b", 5)).as("get(b, 5)").isEqualTo("two");
        assertThat(store.get("a", 10)).as("get(a, 10)").isEqualTo("one");
    }

    @Test
    void timeBeforeFirstValueIsEmpty() {
        Solution store = new Solution();
        store.set("k", "v", 100);
        assertThat(store.get("k", 1)).as("get(k, 1)").isEmpty();
        assertThat(store.get("k", 99)).as("get(k, 99)").isEmpty();
    }

    @Test
    void hugeTimeReturnsTheNewestValue() {
        Solution store = new Solution();
        store.set("k", "old", 1);
        store.set("k", "new", 1_000_000_000);
        assertThat(store.get("k", Integer.MAX_VALUE)).as("get(k, MAX)").isEqualTo("new");
        assertThat(store.get("k", 999_999_999)).as("get(k, 999999999)").isEqualTo("old");
    }

    @Test
    void laterSetDoesNotChangeEarlierAnswers() {
        Solution store = new Solution();
        store.set("k", "first", 2);
        assertThat(store.get("k", 6)).as("before second set").isEqualTo("first");
        store.set("k", "second", 5);
        assertThat(store.get("k", 4)).as("get(k, 4)").isEqualTo("first");
        assertThat(store.get("k", 6)).as("get(k, 6)").isEqualTo("second");
    }

    @Test
    void sameValueStoredAtSeveralTimes() {
        Solution store = new Solution();
        store.set("k", "same", 1);
        store.set("k", "same", 2);
        store.set("k", "other", 3);
        assertThat(store.get("k", 2)).as("get(k, 2)").isEqualTo("same");
        assertThat(store.get("k", 3)).as("get(k, 3)").isEqualTo("other");
    }

    @Test
    void interleavedKeysKeepSeparateHistories() {
        Solution store = new Solution();
        store.set("x", "x1", 1);
        store.set("y", "y1", 2);
        store.set("x", "x2", 3);
        store.set("y", "y2", 4);
        assertThat(store.get("x", 3)).as("get(x, 3)").isEqualTo("x2");
        assertThat(store.get("y", 3)).as("get(y, 3)").isEqualTo("y1");
        assertThat(store.get("y", 4)).as("get(y, 4)").isEqualTo("y2");
        assertThat(store.get("x", 2)).as("get(x, 2)").isEqualTo("x1");
    }

    @Test
    void manyVersionsOfOneKey() {
        Solution store = new Solution();
        for (int i = 0; i < 100; i++) {
            store.set("k", "v" + i, 10 * i + 10);
        }
        for (int time = 0; time <= 1100; time++) {
            String expected = time < 10 ? "" : "v" + Math.min(99, time / 10 - 1);
            assertThat(store.get("k", time)).as("get(k, %d)", time).isEqualTo(expected);
        }
    }

    @Test
    void emptyKeyAndValueAreOrdinary() {
        Solution store = new Solution();
        store.set("", "blank", 7);
        assertThat(store.get("", 7)).as("get(empty key, 7)").isEqualTo("blank");
        assertThat(store.get("", 6)).as("get(empty key, 6)").isEmpty();
    }


}
