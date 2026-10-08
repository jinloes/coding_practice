package com.jinloes.cryptography.envelope;

import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.cryptography.kms.DataKey;
import com.jinloes.cryptography.kms.InMemoryKms;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachingDataKeySourceTest {
    private static final Map<String, String> CONTEXT = Map.of("tenant", "acme");

    private final InMemoryKms kms = new InMemoryKms();
    private final String keyId = kms.createKey();
    private final AtomicInteger kmsCalls = new AtomicInteger();
    private final MutableClock clock = new MutableClock();
    private CachingDataKeySource cache;

    @BeforeEach
    void setUp() {
        DataKeySource counting = (id, ctx) -> {
            kmsCalls.incrementAndGet();
            return kms.generateDataKey(id, ctx);
        };
        cache = new CachingDataKeySource(counting, Duration.ofMinutes(5), 3, clock);
    }

    @Test
    void reusesKeyUntilMaxUses() throws Exception {
        DataKey first = cache.dataKey(keyId, CONTEXT);
        DataKey second = cache.dataKey(keyId, CONTEXT);
        cache.dataKey(keyId, CONTEXT);
        DataKey fourth = cache.dataKey(keyId, CONTEXT);

        assertThat(second.plaintext()).isEqualTo(first.plaintext());
        assertThat(fourth.plaintext()).isNotEqualTo(first.plaintext());
        assertThat(kmsCalls).hasValue(2);
    }

    @Test
    void expiresByAge() throws Exception {
        cache.dataKey(keyId, CONTEXT);
        clock.advance(Duration.ofMinutes(5));
        cache.dataKey(keyId, CONTEXT);

        assertThat(kmsCalls).hasValue(2);
    }

    @Test
    void neverSharesKeysAcrossContexts() throws Exception {
        DataKey a = cache.dataKey(keyId, CONTEXT);
        DataKey b = cache.dataKey(keyId, Map.of("tenant", "other"));

        assertThat(a.plaintext()).isNotEqualTo(b.plaintext());
    }

    @Test
    void callerZeroingReturnedKeyDoesNotCorruptCache() throws Exception {
        EnvelopeEncryptor encryptor = new EnvelopeEncryptor(kms, cache, keyId);
        byte[] first = encryptor.encrypt("one".getBytes(), CONTEXT);
        byte[] second = encryptor.encrypt("two".getBytes(), CONTEXT);

        assertThat(encryptor.decrypt(first, CONTEXT)).isEqualTo("one".getBytes());
        assertThat(encryptor.decrypt(second, CONTEXT)).isEqualTo("two".getBytes());
        assertThat(kmsCalls).hasValue(1);
    }

    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2025-01-01T00:00:00Z");

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }
    }
}
