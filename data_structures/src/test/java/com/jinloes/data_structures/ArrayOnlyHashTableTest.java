package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayOnlyHashTableTest {

    private ArrayOnlyHashTable<String, String> hashTable;

    @BeforeAll
    public void setUp() {
        hashTable = new ArrayOnlyHashTable<>(1, 1);
    }

    @Test
    public void testPutAndGet() {
        hashTable.put("key", "value");
        hashTable.put("key1", "value1");
        hashTable.put("key2", "value2");
        hashTable.put("key3", "value3");
        hashTable.put("key4", "value4");

        assertThat(hashTable.get("key")).hasValue("value");
        assertThat(hashTable.get("key1")).hasValue("value1");
        assertThat(hashTable.get("key2")).hasValue("value2");
        assertThat(hashTable.get("key3")).hasValue("value3");
        assertThat(hashTable.get("key4")).hasValue("value4");
    }

    @Test
    public void testDelete() {
        hashTable.put("key", "value");
        hashTable.put("key1", "value1");

        assertThat(hashTable.get("key")).hasValue("value");
        assertThat(hashTable.get("key1")).hasValue("value1");

        hashTable.delete("key");

        assertThat(hashTable.get("key")).isEmpty();
        assertThat(hashTable.get("key1")).hasValue("value1");
    }

    @Test
    public void testContainsKey() {
        hashTable.put("key", "value");
        hashTable.put("key1", "value1");
        hashTable.put("key2", "value2");
        hashTable.put("key3", "value3");
        hashTable.put("key4", "value4");

        assertThat(hashTable.containsKey("key")).isTrue();
        assertThat(hashTable.containsKey("key1")).isTrue();
        assertThat(hashTable.containsKey("key2")).isTrue();
        assertThat(hashTable.containsKey("key3")).isTrue();
        assertThat(hashTable.containsKey("key4")).isTrue();
        assertThat(hashTable.containsKey("asdfadsfsad")).isFalse();
    }
}