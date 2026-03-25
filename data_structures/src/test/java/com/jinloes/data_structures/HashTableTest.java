package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for HashTable
 */
class HashTableTest {
    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    void createEmptyHashTableWithDefaultCapacity() {
        HashTable<String, Integer> emptyTable = new HashTable<>();
        assertEquals(0, emptyTable.getSize(), "Size should be 0 for default capacity");
    }

    @Test
    void createEmptyHashTableWithSpecifiedCapacity() {
        HashTable<String, Integer> emptyTable = new HashTable<>(5);
        assertEquals(0, emptyTable.getSize(), "Size should be 0 for specified capacity");
    }

    @Test
    void addItemToHashTable() {
        // When: insert a number into the hash table
        table.put("a", 5);

        // Then: the item should exist in the hash table
        assertEquals(1, table.getSize(), "Size should be 1 after adding item");
        assertTrue(table.containsKey("a"), "Table should contain key 'a'");
        assertTrue(table.containsValue(5), "Table should contain value 5");
        assertEquals(5, (int) table.get("a"), "Get should return 5 for key 'a'");
    }

    @Test
    void handleCollisions() {
        // Given: a hash table with small capacity
        HashTable<String, Integer> table = new HashTable<>(1);

        // When: insert two values
        table.put("a", 5);
        table.put("b", 6);

        // Then: Both values should exist in the hash table
        assertEquals(5, (int) table.get("a"), "Get should return 5 for key 'a'");
        assertEquals(6, (int) table.get("b"), "Get should return 6 for key 'b'");
        assertEquals(2, table.getSize(), "Size should be 2 after adding two items");
    }

    @Test
    void removeItemThatExists() {
        table.put("a", 5);

        // When: remove key from the hash table
        Integer removedItem = table.remove("a");

        // Then: the key should not exist in the hash table
        assertEquals(5, (int) removedItem, "Removed item should be 5");
        assertFalse(table.containsKey("a"), "Table should not contain key 'a' after removal");
    }

    @Test
    void removeItemThatDoesNotExist() {
        // When: remove key from the hash table
        Integer removedItem = table.remove("a");

        // Then: the key should not exist in the hash table
        assertNull(removedItem, "Removed item should be null for non-existent key");
        assertFalse(table.containsKey("a"), "Table should not contain key 'a'");
    }

    @Nested
    class EdgeCaseTests {
        @Test
        void putNullKey() {
            assertThrows(NullPointerException.class, () -> table.put(null, 5),
                "Should throw NullPointerException for null key");
        }

        @Test
        void containsKeyNull() {
            assertThrows(NullPointerException.class, () -> table.containsKey(null),
                "Should throw NullPointerException for null key");
        }

        @Test
        void getNullKey() {
            assertThrows(NullPointerException.class, () -> table.get(null),
                "Should throw NullPointerException for null key");
        }

        @Test
        void removeNullKey() {
            assertThrows(NullPointerException.class, () -> table.remove(null),
                "Should throw NullPointerException for null key");
        }

        @Test
        void putExistingKeyUpdatesValue() {
            table.put("a", 5);
            assertEquals(1, table.getSize(), "Size should remain 1 after updating existing key");

            table.put("a", 10);
            assertEquals(1, table.getSize(), "Size should remain 1 after updating existing key");
            assertEquals(10, (int) table.get("a"), "Value should be updated to 10");
            assertTrue(table.containsValue(10), "Table should contain new value 10");
            assertFalse(table.containsValue(5), "Table should not contain old value 5");
        }

        @Test
        void containsValueReturnsFalse() {
            assertFalse(table.containsValue(5), "Empty table should not contain value");
            table.put("a", 5);
            assertFalse(table.containsValue(6), "Table should not contain different value");
        }

        @Test
        void isEmptyReturnsTrueForEmptyTable() {
            assertTrue(table.isEmpty(), "Empty table should return true for isEmpty()");
        }

        @Test
        void isEmptyReturnsFalseForNonEmptyTable() {
            table.put("a", 5);
            assertFalse(table.isEmpty(), "Non-empty table should return false for isEmpty()");
        }
    }

    @Nested
    class ConstructorTests {
        @Test
        void constructorWithZeroCapacity() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable<>(0),
                "Should throw IllegalArgumentException for zero capacity");
        }

        @Test
        void constructorWithNegativeCapacity() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable<>(-1),
                "Should throw IllegalArgumentException for negative capacity");
        }

        @Test
        void constructorWithZeroCapacityForString() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable<String, Integer>(0),
                "Should throw IllegalArgumentException for zero capacity");
        }

        @Test
        void constructorWithNegativeCapacityForString() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable<String, Integer>(-1),
                "Should throw IllegalArgumentException for negative capacity");
        }

        @Test
        void constructorWithPositiveCapacity() {
            HashTable<String, Integer> table = new HashTable<>(10);
            assertEquals(0, table.getSize(), "New table should have size 0");
            assertFalse(table.isEmpty(), "New table should not be empty");
        }
    }
}