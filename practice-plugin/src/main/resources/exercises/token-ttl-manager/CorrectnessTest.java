package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyManagerReturnsZero() {
        Solution manager = new Solution(5);
        assertThat(manager.countUnexpired(1)).isEqualTo(0);
        manager.renew("missing", 2);
        assertThat(manager.countUnexpired(3)).isEqualTo(0);
    }

    @Test
    void renewingOlderTokenMovesItBehindNewerTokenInExpirationOrder() {
        Solution manager = new Solution(10);
        manager.generate("a", 1);   // expires 11
        manager.generate("b", 5);   // expires 15
        manager.renew("a", 9);      // now expires 19 (after b!)
        assertThat(manager.countUnexpired(14)).isEqualTo(2);
        assertThat(manager.countUnexpired(15)).isEqualTo(1); // b expired, a still live
        assertThat(manager.countUnexpired(18)).isEqualTo(1);
        assertThat(manager.countUnexpired(19)).isEqualTo(0);
    }

    @Test
    void expiredTokenCannotBeRenewedLater() {
        Solution manager = new Solution(3);
        manager.generate("t", 1);   // expires 4
        manager.renew("t", 4);      // expired at 4 -> ignored
        manager.renew("t", 5);      // still ignored
        assertThat(manager.countUnexpired(6)).isEqualTo(0);
    }
}
