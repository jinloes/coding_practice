package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void carriesUnusedSlotFromPreviousWindowIntoBurstAllowance() {
        Solution limiter = new Solution(2, 10, 3);
        assertThat(List.of(
                limiter.allow("api", 1),
                limiter.allow("api", 12),
                limiter.allow("api", 13),
                limiter.allow("api", 14),
                limiter.allow("api", 15)))
                .containsExactly(true, true, true, true, false);
    }

    @Test
    void capsAccumulatedCreditsAtConfiguredMaximumAcrossIdleWindows() {
        Solution limiter = new Solution(2, 5, 3);
        assertThat(List.of(
                limiter.allow("svc", 0),
                limiter.allow("svc", 20),
                limiter.allow("svc", 20),
                limiter.allow("svc", 21),
                limiter.allow("svc", 22),
                limiter.allow("svc", 23),
                limiter.allow("svc", 24)))
                .containsExactly(true, true, true, true, true, true, false);
    }

    @Test
    void spendingCreditsLeavesZeroUnusedBaseForTheFollowingWindow() {
        Solution limiter = new Solution(1, 10, 2);
        assertThat(List.of(
                limiter.allow("web", 2),
                limiter.allow("web", 22),
                limiter.allow("web", 23),
                limiter.allow("web", 32),
                limiter.allow("web", 33)))
                .containsExactly(true, true, true, true, false);
    }
}
