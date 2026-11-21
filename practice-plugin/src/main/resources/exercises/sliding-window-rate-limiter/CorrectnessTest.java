package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void repeatedRejectionsNeverExtendLockoutPastOriginalAcceptedRequests() {
        Solution limiter = new Solution(2, 10);
        assertThat(limiter.allow("c1", 100)).isTrue();
        assertThat(limiter.allow("c1", 102)).isTrue();
        for (long t = 103; t <= 109; t++) {
            assertThat(limiter.allow("c1", t)).as("rejected at %d", t).isFalse();
        }
        assertThat(limiter.allow("c1", 110)).isTrue();
        assertThat(limiter.allow("c1", 111)).isFalse();
        assertThat(limiter.allow("c1", 112)).isTrue();
    }

    @Test
    void supportsLargeTimestampsWithoutIntegerOverflow() {
        Solution limiter = new Solution(1, 60);
        long start = 10_000_000_000L;
        assertThat(limiter.allow("edge", start)).isTrue();
        assertThat(limiter.allow("edge", start + 59)).isFalse();
        assertThat(limiter.allow("edge", start + 60)).isTrue();
    }

    @Test
    void interleavesManyClientsIndependently() {
        Solution limiter = new Solution(2, 5);
        for (int client = 0; client < 10; client++) {
            String id = "client-" + client;
            assertThat(limiter.allow(id, 1)).isTrue();
            assertThat(limiter.allow(id, 2)).isTrue();
            assertThat(limiter.allow(id, 5)).isFalse();
            assertThat(limiter.allow(id, 6)).isTrue();
        }
    }

    @Test
    void validatesConfigurationAndRequestArguments() {
        assertThatThrownBy(() -> new Solution(0, 10)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Solution(2, 0)).isInstanceOf(IllegalArgumentException.class);
        Solution limiter = new Solution(2, 10);
        assertThatThrownBy(() -> limiter.allow("", 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> limiter.allow(null, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> limiter.allow("api", -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
