package com.jinloes.system_design.coordination;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FencedLeaseTest {
    @Test
    void monotonicFencingTokensPreventGcPausedZombieLeaderFromCorruptingResource() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        FencedLeaseCoordinator coordinator = new FencedLeaseCoordinator(clock);
        FencedResource<String> storage = new FencedResource<>("initial");

        // Leader A acquires lease (token 1) and writes.
        var leaseA = coordinator.tryAcquireOrRenew("partition-7", "leader-a", Duration.ofSeconds(10)).orElseThrow();
        assertThat(leaseA.fencingToken()).isEqualTo(1L);
        storage.write(leaseA.fencingToken(), "batch-from-a-1");

        // While lease A is still valid, Leader B cannot steal it, and Leader A can renew keeping token 1.
        clock.advance(Duration.ofSeconds(5));
        assertThat(coordinator.tryAcquireOrRenew("partition-7", "leader-b", Duration.ofSeconds(10))).isEmpty();
        var renewedA = coordinator.tryAcquireOrRenew("partition-7", "leader-a", Duration.ofSeconds(10)).orElseThrow();
        assertThat(renewedA.fencingToken()).isEqualTo(1L);

        // Simulate a long GC pause on Leader A past its lease expiry; Leader B acquires token 2 and writes.
        clock.advance(Duration.ofSeconds(11));
        var leaseB = coordinator.tryAcquireOrRenew("partition-7", "leader-b", Duration.ofSeconds(10)).orElseThrow();
        assertThat(leaseB.fencingToken()).isEqualTo(2L);
        storage.write(leaseB.fencingToken(), "batch-from-b-2");

        // Leader A wakes up from its GC pause still holding stale token 1: storage rejects the write!
        assertThatThrownBy(() -> storage.write(leaseA.fencingToken(), "zombie-overwrite-from-a"))
            .isInstanceOf(FencedResource.StaleFencingTokenException.class);
        assertThat(storage.read()).isEqualTo("batch-from-b-2");
        assertThat(storage.highestSeenToken()).isEqualTo(2L);

        // Explicit release with stale token fails; release with active owner + token succeeds.
        assertThat(coordinator.release("unknown-partition", "leader-a", 1L)).isFalse();
        assertThat(coordinator.release("partition-7", "leader-a", leaseA.fencingToken())).isFalse();
        assertThat(coordinator.release("partition-7", "leader-b", 999L)).isFalse();
        assertThat(coordinator.release("partition-7", "leader-b", leaseB.fencingToken())).isTrue();

        assertThatThrownBy(() -> storage.write(0L, "invalid"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> coordinator.tryAcquireOrRenew("partition-7", "leader-a", Duration.ZERO))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> coordinator.tryAcquireOrRenew("partition-7", "leader-a", Duration.ofSeconds(-1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        MutableClock(Instant initial) {
            this.instant = initial;
        }

        void advance(Duration delta) {
            instant = instant.plus(delta);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
