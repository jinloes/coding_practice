package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void answersWithTheLatestValueAtOrBeforeTheTime() {
        Solution store = new Solution();
        store.set("cat", "meow", 5);
        assertThat(store.get("cat", 5)).as("get(cat, 5)").isEqualTo("meow");
        assertThat(store.get("cat", 4)).as("get(cat, 4)").isEmpty();
        assertThat(store.get("cat", 9)).as("get(cat, 9)").isEqualTo("meow");
    }

    @Test
    void keepsEarlierValuesAvailable() {
        Solution store = new Solution();
        store.set("a", "x", 1);
        store.set("a", "y", 4);
        assertThat(store.get("a", 3)).as("get(a, 3)").isEqualTo("x");
        assertThat(store.get("a", 4)).as("get(a, 4)").isEqualTo("y");
        assertThat(store.get("a", 1)).as("get(a, 1)").isEqualTo("x");
    }

    @Test
    void unknownKeyIsEmpty() {
        Solution store = new Solution();
        assertThat(store.get("ghost", 1)).as("get(ghost, 1)").isEmpty();
    }


}
