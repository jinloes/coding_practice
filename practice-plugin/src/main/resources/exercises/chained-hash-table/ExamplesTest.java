package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void storesAndReadsAKey() {
        Solution table = new Solution(4);
        table.put("a", "1");
        assertThat(table.get("a")).isEqualTo("1");
        assertThat(table.size()).isEqualTo(1);
    }

    @Test
    void overwriteDoesNotAddASecondEntry() {
        Solution table = new Solution(4);
        table.put("a", "1");
        table.put("a", "2");
        assertThat(table.get("a")).isEqualTo("2");
        assertThat(table.size()).isEqualTo(1);
    }

    @Test
    void removeReportsWhetherAKeyWasPresent() {
        Solution table = new Solution(2);
        table.put("a", "1");
        assertThat(table.remove("a")).isTrue();
        assertThat(table.remove("a")).isFalse();
    }
}
