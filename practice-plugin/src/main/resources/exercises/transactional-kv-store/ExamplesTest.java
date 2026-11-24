package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void rollsBackOverwrittenKey() {
        Solution store = new Solution();
        store.set("a", "1");
        store.begin();
        store.set("a", "2");
        assertThat(store.get("a")).isEqualTo("2");
        assertThat(store.rollback()).isTrue();
        assertThat(store.get("a")).isEqualTo("1");
    }

    @Test
    void shadowsOuterKeyOnDeleteAndRestoresOnRollback() {
        Solution store = new Solution();
        store.set("a", "1");
        store.begin();
        store.delete("a");
        assertThat(store.get("a")).isNull();
        assertThat(store.rollback()).isTrue();
        assertThat(store.get("a")).isEqualTo("1");
    }

    @Test
    void commitsInnerTransactionIntoOuterThenRollsBackOuter() {
        Solution store = new Solution();
        store.begin();
        store.set("x", "10");
        store.begin();
        store.set("x", "20");
        assertThat(store.commit()).isTrue();
        assertThat(store.get("x")).isEqualTo("20");
        assertThat(store.rollback()).isTrue();
        assertThat(store.get("x")).isNull();
    }
}
