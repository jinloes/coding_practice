package com.jinloes.data_structures;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class LRUTest {
  private LRU<String, String> lru;

  @Before public void setUp() throws Exception {
    lru = new LRU<>(5);
  }

  @Test public void testAdd() {
    IntStream.range(0, 6).mapToObj(Integer::toString).forEach(val -> lru.add(val, val));
    assertThat(lru.getEntries()).containsExactly(entry("1", "1"), entry("2", "2"), entry("3", "3"),
        entry("4", "4"), entry("5 ", "5"));

  }

}