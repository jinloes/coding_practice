package com.jinloes.hash_tables;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashNodeTest {

    @Test
    void accessorsReturnConstructorValues() {
        ArrayOnlyHashTable.HashNode<String, Integer> node =
                new ArrayOnlyHashTable.HashNode<>("key", 42);

        assertThat(node.getKey()).isEqualTo("key");
        assertThat(node.getValue()).isEqualTo(42);
    }

    @Test
    void keyOnlyConstructorHasNullValue() {
        ArrayOnlyHashTable.HashNode<String, Integer> node =
                new ArrayOnlyHashTable.HashNode<>("key");

        assertThat(node.getKey()).isEqualTo("key");
        assertThat(node.getValue()).isNull();
    }

    @Test
    void equalsAndHashCodeDependOnKeyOnly() {
        ArrayOnlyHashTable.HashNode<String, Integer> a =
                new ArrayOnlyHashTable.HashNode<>("key", 1);
        ArrayOnlyHashTable.HashNode<String, Integer> sameKey =
                new ArrayOnlyHashTable.HashNode<>("key", 999);
        ArrayOnlyHashTable.HashNode<String, Integer> otherKey =
                new ArrayOnlyHashTable.HashNode<>("other", 1);

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(sameKey);
        assertThat(a).hasSameHashCodeAs(sameKey);
        assertThat(a).isNotEqualTo(otherKey);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("not a node");
    }

    @Test
    void toStringContainsKeyAndValue() {
        ArrayOnlyHashTable.HashNode<String, Integer> node =
                new ArrayOnlyHashTable.HashNode<>("key", 42);

        assertThat(node.toString()).contains("key").contains("42");
    }
}
