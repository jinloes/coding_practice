package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void readingAKeyProtectsItFromEviction() {
        Solution cache = new Solution(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assertThat(cache.get(1)).as("get(1)").isEqualTo(10);
        cache.put(3, 30);
        assertThat(cache.get(2)).as("key 2 was least recently used and must be evicted").isEqualTo(-1);
        assertThat(cache.get(1)).as("get(1)").isEqualTo(10);
        assertThat(cache.get(3)).as("get(3)").isEqualTo(30);
    }

    @Test
    void capacityOneKeepsOnlyTheNewestKey() {
        Solution cache = new Solution(1);
        cache.put(1, 5);
        cache.put(2, 6);
        assertThat(cache.get(1)).as("get(1) after it was evicted").isEqualTo(-1);
        assertThat(cache.get(2)).as("get(2)").isEqualTo(6);
    }

    @Test
    void overwritingAKeyUpdatesAndRefreshesIt() {
        Solution cache = new Solution(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 100);
        cache.put(3, 3);
        assertThat(cache.get(1)).as("get(1) after overwrite").isEqualTo(100);
        assertThat(cache.get(2)).as("get(2) was evicted").isEqualTo(-1);
        assertThat(cache.get(3)).as("get(3)").isEqualTo(3);
    }
}
