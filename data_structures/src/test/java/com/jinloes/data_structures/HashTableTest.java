package com.jinloes.data_structures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashTableTest {
    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    void addItem() {
        table.put("a", 5);

        assertThat(table.getSize()).isEqualTo(1);
        assertThat(table.containsKey("a")).isTrue();
        assertThat(table.containsValue(5)).isTrue();
        assertThat(table.get("a")).isEqualTo(5);
    }

    @Test
    void handleCollisions() {
        HashTable<String, Integer> table = new HashTable<>(1);

        table.put("a", 5);
        table.put("b", 6);

        assertThat(table.get("a")).isEqualTo(5);
        assertThat(table.get("b")).isEqualTo(6);
        assertThat(table.getSize()).isEqualTo(2);
    }

    @Test
    void removeExistingItem() {
        table.put("a", 5);

        Integer removed = table.remove("a");

        assertThat(removed).isEqualTo(5);
        assertThat(table.containsKey("a")).isFalse();
    }

    @Test
    void removeMissingItemReturnsNull() {
        assertThat(table.remove("a")).isNull();
    }

    @Nested
    class ConstructorTests {
        @Test
        void defaultCapacity() {
            HashTable<String, Integer> emptyTable = new HashTable<>();

            assertThat(emptyTable.getSize()).isZero();
            assertThat(emptyTable.isEmpty()).isTrue();
        }

        @Test
        void specifiedCapacity() {
            HashTable<String, Integer> emptyTable = new HashTable<>(5);

            assertThat(emptyTable.getSize()).isZero();
            assertThat(emptyTable.isEmpty()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void invalidCapacityThrows(int capacity) {
            assertThatThrownBy(() -> new HashTable<>(capacity))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class EdgeCaseTests {
        @Test
        void nullKeyThrows() {
            assertThatThrownBy(() -> table.put(null, 5)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> table.get(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> table.containsKey(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> table.remove(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        void putExistingKeyUpdatesValue() {
            table.put("a", 5);
            table.put("a", 10);

            assertThat(table.getSize()).isEqualTo(1);
            assertThat(table.get("a")).isEqualTo(10);
            assertThat(table.containsValue(10)).isTrue();
            assertThat(table.containsValue(5)).isFalse();
        }

        @Test
        void containsValueReturnsFalseForEmptyTable() {
            assertThat(table.containsValue(5)).isFalse();
        }

        @Test
        void containsValueReturnsFalseForMissingValue() {
            table.put("a", 5);
            assertThat(table.containsValue(6)).isFalse();
        }

        @Test
        void isEmpty() {
            assertThat(table.isEmpty()).isTrue();
            table.put("a", 5);
            assertThat(table.isEmpty()).isFalse();
        }
    }
}