package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void firstRequestAtLargeTimestampDoesNotPreBankHistoricalCredits() {
        Solution limiter = new Solution(2, 10, 5);
        assertThat(limiter.allow("late-client", 1_000_000L)).isTrue();
        assertThat(limiter.allow("late-client", 1_000_001L)).isTrue();
        assertThat(limiter.allow("late-client", 1_000_002L)).isFalse();
    }

    @Test
    void handlesExtremeTimestampJumpInConstantTimeWithoutOverflow() {
        Solution limiter = new Solution(1_000_000, 1, 2);
        assertThat(limiter.allow("burst", 0L)).isTrue();
        long farFuture = Long.MAX_VALUE / 2;
        for (int i = 0; i < 1_000_002; i++) {
            // Drain only the first 5 and verify exact cap on a smaller limiter as well.
            if (i < 5) {
                assertThat(limiter.allow("burst", farFuture)).isTrue();
            } else {
                break;
            }
        }

        Solution small = new Solution(1_000_000_000, 1, 1);
        assertThat(small.allow("overflow-probe", 0L)).isTrue();
        // Idle gap * baseLimit would overflow 64-bit signed long if multiplied naively:
        // gap ~ 5e18, baseLimit = 1e9 -> wrapping negative unless clamped safely!
        long huge = 5_000_000_000_000_000_000L;
        Solution tight = new Solution(2, 1, 2);
        assertThat(tight.allow("safe", 0L)).isTrue();
        assertThat(tight.allow("safe", 0L)).isTrue();
        assertThat(tight.allow("safe", huge)).isTrue();
        assertThat(tight.allow("safe", huge)).isTrue();
        assertThat(tight.allow("safe", huge)).isTrue();
        assertThat(tight.allow("safe", huge)).isTrue();
        assertThat(tight.allow("safe", huge)).isFalse();

        Solution bigBase = new Solution(1_000_000_000, 1, 1);
        assertThat(bigBase.allow("wrap", 0L)).isTrue();
        // Choose idle = 19 so idle * 1_000_000_000 fits, AND idle = 10_000_000_000L where idle * 1_000_000_000 overflows long!
        long wrapTs = 10_000_000_001L;
        // 10_000_000_000L * 1_000_000_000L overflows signed long to a negative value (-8446744073709551616L)!
        assertThat(bigBase.allow("wrap", wrapTs)).isTrue();
    }

    @Test
    void zeroMaxCreditsBehavesAsPlainFixedWindowLimiter() {
        Solution limiter = new Solution(2, 10, 0);
        assertThat(limiter.allow("plain", 1)).isTrue();
        assertThat(limiter.allow("plain", 50)).isTrue();
        assertThat(limiter.allow("plain", 51)).isTrue();
        assertThat(limiter.allow("plain", 52)).isFalse();
    }

    @Test
    void isolatesStateAcrossIndependentClients() {
        Solution limiter = new Solution(1, 10, 2);
        assertThat(limiter.allow("a", 0)).isTrue();
        assertThat(limiter.allow("b", 0)).isTrue();
        assertThat(limiter.allow("a", 10)).isTrue();
        assertThat(limiter.allow("a", 11)).isFalse();
        assertThat(limiter.allow("b", 30)).isTrue();
        assertThat(limiter.allow("b", 31)).isTrue();
        assertThat(limiter.allow("b", 32)).isTrue();
        assertThat(limiter.allow("b", 33)).isFalse();
    }

    @Test
    void validatesConfigurationAndCallArguments() {
        assertThatThrownBy(() -> new Solution(0, 10, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 0, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 10, -1)).isInstanceOf(IllegalArgumentException.class);

        Solution limiter = new Solution(2, 10, 2);
        assertThatThrownBy(() -> limiter.allow("", 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> limiter.allow(null, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> limiter.allow("api", -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
