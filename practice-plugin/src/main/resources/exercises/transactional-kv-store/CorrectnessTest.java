package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void commitAndRollbackReturnFalseWithoutActiveTransaction() {
        Solution store = new Solution();
        assertThat(store.commit()).isFalse();
        assertThat(store.rollback()).isFalse();
        assertThat(store.get("missing")).isNull();
    }

    @Test
    void commitsDeleteToBaseStore() {
        Solution store = new Solution();
        store.set("k", "v1");
        store.begin();
        store.delete("k");
        assertThat(store.commit()).isTrue();
        assertThat(store.get("k")).isNull();
        assertThat(store.rollback()).isFalse();
    }

    @Test
    void commitsDeleteAcrossNestedTransactions() {
        Solution store = new Solution();
        store.set("k", "base");
        store.begin();
        store.set("k", "outer");
        store.begin();
        store.delete("k");
        assertThat(store.get("k")).isNull();
        assertThat(store.commit()).isTrue();
        assertThat(store.get("k")).isNull();
        assertThat(store.commit()).isTrue();
        assertThat(store.get("k")).isNull();
    }

    @Test
    void setAfterDeleteInSameTransactionRestoresVisibility() {
        Solution store = new Solution();
        store.set("k", "v1");
        store.begin();
        store.delete("k");
        assertThat(store.get("k")).isNull();
        store.set("k", "v2");
        assertThat(store.get("k")).isEqualTo("v2");
        assertThat(store.commit()).isTrue();
        assertThat(store.get("k")).isEqualTo("v2");
    }

    @Test
    void multipleIndependentKeysAndSequentialTransactions() {
        Solution store = new Solution();
        store.set("a", "1");
        store.set("b", "2");
        store.begin();
        store.set("a", "10");
        store.set("c", "30");
        assertThat(store.commit()).isTrue();

        store.begin();
        store.delete("b");
        store.set("c", "99");
        assertThat(store.rollback()).isTrue();

        assertThat(store.get("a")).isEqualTo("10");
        assertThat(store.get("b")).isEqualTo("2");
        assertThat(store.get("c")).isEqualTo("30");
    }
}
