package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LRUTest {
    private LRU<String, String> lru;

    @BeforeEach
    public void setUp() throws Exception {
        lru = new LRU<>(5);
    }

    @Test
    public void testAdd() {
        IntStream.range(0, 6)
                .mapToObj(Integer::toString)
                .forEach(val -> lru.add(val, val));


        assertThat(lru)
                .isEqualTo(new LRU<String, String>(5)
                        .add("1", "1")
                        .add("2", "2")
                        .add("3", "3")
                        .add("4", "4")
                        .add("5", "5")
                );

        lru.add("7", "7");


        assertThat(lru)
                .isEqualTo(new LRU<String, String>(5)
                        .add("2", "2")
                        .add("3", "3")
                        .add("4", "4")
                        .add("5", "5")
                        .add("7", "7")
                );
    }

    @Test
    public void testGet() {
        IntStream.range(0, 6)
                .mapToObj(Integer::toString)
                .forEach(val -> lru.add(val, val));


        assertThat(lru)
                .isEqualTo(new LRU<String, String>(5)
                        .add("1", "1")
                        .add("2", "2")
                        .add("3", "3")
                        .add("4", "4")
                        .add("5", "5")
                );

        assertThat(lru.get("1")).isEqualTo("1");


        assertThat(lru)
                .isEqualTo(new LRU<String, String>(5)
                        .add("2", "2")
                        .add("3", "3")
                        .add("4", "4")
                        .add("5", "5")
                        .add("1", "1")
                );
    }

}