package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void handlesGenerateRenewAndExpirationSequence() {
        Solution manager = new Solution(5);
        manager.renew("a", 1);
        manager.generate("a", 2);
        assertThat(manager.countUnexpired(6)).isEqualTo(1);
        manager.generate("b", 7);
        manager.renew("a", 8);
        manager.renew("b", 10);
        assertThat(manager.countUnexpired(15)).isEqualTo(0);
    }

    @Test
    void expirationTakesPrecedenceAtExactBoundary() {
        Solution manager = new Solution(5);
        manager.generate("x", 1);
        manager.renew("x", 6);
        assertThat(manager.countUnexpired(6)).isEqualTo(0);
    }

    @Test
    void countsMultipleActiveTokensAcrossExpirations() {
        Solution manager = new Solution(10);
        manager.generate("a", 1);
        manager.generate("b", 5);
        assertThat(manager.countUnexpired(10)).isEqualTo(2);
        assertThat(manager.countUnexpired(11)).isEqualTo(1);
    }
}
