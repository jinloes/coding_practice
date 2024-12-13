package com.jinloes.data_structures;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeMapTest {
    private TimeMap timeMap;

    @BeforeEach
    public void setUp() throws Exception {
        timeMap = new TimeMap();
    }

    @Test
    public void testOperations() {
        timeMap.set("foo", "bar", 1);

        assertThat(timeMap.get("foo", 1)).isEqualTo("bar");
        assertThat(timeMap.get("foo", 3)).isEqualTo("bar");

        timeMap.set("foo", "bar2", 4);

        assertThat(timeMap.get("foo", 4)).isEqualTo("bar2");

        assertThat(timeMap.get("foo", 5)).isEqualTo("bar2");
    }
}