package com.jinloes.hash_tables;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArrayOnlyHashTableTest {

    private ArrayOnlyHashTable<String, String> hashTable;

    @BeforeEach
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

    @Test
    public void putOverwritesExistingKey() {
        hashTable.put("key", "value");
        hashTable.put("key", "updated");

        assertThat(hashTable.get("key")).hasValue("updated");
    }

    @Test
    public void nullKeyThrows() {
        assertThatThrownBy(() -> hashTable.put(null, "value"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void keysWithNegativeHashCodeDoNotCrash() {
        // "polygenelubricants" is a well-known String whose hashCode() is negative.
        hashTable.put("polygenelubricants", "value");

        assertThat(hashTable.get("polygenelubricants")).hasValue("value");
    }

    @Test
    public void defaultConstructorStoresAndRetrieves() {
        ArrayOnlyHashTable<String, String> defaultTable = new ArrayOnlyHashTable<>();

        defaultTable.put("key", "value");

        assertThat(defaultTable.get("key")).hasValue("value");
    }

    @Test
    public void collidingKeysFillSuccessiveBucketSlots() {
        // Capacity 1 forces every key into the same bucket; multiple slots exercise the insert scan.
        ArrayOnlyHashTable<String, String> singleBucket = new ArrayOnlyHashTable<>(1, 4);

        singleBucket.put("a", "1");
        singleBucket.put("b", "2");
        singleBucket.put("c", "3");

        assertThat(singleBucket.get("a")).hasValue("1");
        assertThat(singleBucket.get("b")).hasValue("2");
        assertThat(singleBucket.get("c")).hasValue("3");
    }

    @Test
    public void fullBucketGrowsToFitMoreEntries() {
        // Bucket capacity 2 in a single bucket: the third colliding key forces a grow.
        ArrayOnlyHashTable<String, String> singleBucket = new ArrayOnlyHashTable<>(1, 2);

        singleBucket.put("a", "1");
        singleBucket.put("b", "2");
        singleBucket.put("c", "3");

        assertThat(singleBucket.get("a")).hasValue("1");
        assertThat(singleBucket.get("b")).hasValue("2");
        assertThat(singleBucket.get("c")).hasValue("3");
    }

    @Test
    public void getMissingKeyReturnsEmpty() {
        assertThat(hashTable.get("absent")).isEmpty();
    }

    @Test
    public void deleteMissingKeyIsNoOp() {
        hashTable.delete("absent");

        assertThat(hashTable.containsKey("absent")).isFalse();
    }
}