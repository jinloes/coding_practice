package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void rejectedRequestsDoNotConsumeFutureWindowSlots() {
        Solution limiter = new Solution(2, 10);
        List<Boolean> decisions = List.of(
                limiter.allow("api", 1),
                limiter.allow("api", 5),
                limiter.allow("api", 10),
                limiter.allow("api", 11));

        assertThat(decisions).containsExactly(true, true, false, true);
    }

    @Test
    void isolatesQuotasPerClientAndExpiresExactCutoff() {
        Solution limiter = new Solution(1, 5);
        List<Boolean> decisions = List.of(
                limiter.allow("alice", 0),
                limiter.allow("bob", 0),
                limiter.allow("alice", 4),
                limiter.allow("alice", 5));

        assertThat(decisions).containsExactly(true, true, false, true);
    }

    @Test
    void countsSimultaneousAcceptedRequestsWithinTheSameSecond() {
        Solution limiter = new Solution(3, 4);
        List<Boolean> decisions = List.of(
                limiter.allow("svc", 2),
                limiter.allow("svc", 2),
                limiter.allow("svc", 2),
                limiter.allow("svc", 2),
                limiter.allow("svc", 6));

        assertThat(decisions).containsExactly(true, true, true, false, true);
    }
}
