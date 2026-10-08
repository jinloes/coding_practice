package com.jinloes.cryptography.envelope;

import com.jinloes.cryptography.kms.DataKey;
import com.jinloes.cryptography.kms.EncryptionContext;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Reuses a data key for a bounded number of messages and a bounded time to cut KMS calls.
 *
 * <p>The trade-off: fewer KMS round trips (latency, cost, rate limits, availability) versus a larger
 * blast radius if one data key leaks, and less granular audit logs. Limits are per
 * (keyId, encryption context) so keys are never shared across contexts.
 */
public final class CachingDataKeySource implements DataKeySource {
    private final DataKeySource delegate;
    private final Duration maxAge;
    private final long maxUses;
    private final Clock clock;
    private final Map<String, Entry> cache = new HashMap<>();

    public CachingDataKeySource(DataKeySource delegate, Duration maxAge, long maxUses, Clock clock) {
        if (maxUses < 1) {
            throw new IllegalArgumentException("maxUses must be positive");
        }
        this.delegate = delegate;
        this.maxAge = maxAge;
        this.maxUses = maxUses;
        this.clock = clock;
    }

    @Override
    public synchronized DataKey dataKey(String keyId, Map<String, String> context) throws GeneralSecurityException {
        String cacheKey = keyId + "|" + Base64.getEncoder().encodeToString(EncryptionContext.canonicalize(context));
        Instant now = clock.instant();
        Entry entry = cache.get(cacheKey);
        if (entry == null || entry.uses >= maxUses || !now.isBefore(entry.created.plus(maxAge))) {
            entry = new Entry(delegate.dataKey(keyId, context), now);
            cache.put(cacheKey, entry);
        }
        entry.uses++;
        return new DataKey(entry.key.plaintext().clone(), entry.key.wrapped().clone());
    }

    private static final class Entry {
        private final DataKey key;
        private final Instant created;
        private long uses;

        Entry(DataKey key, Instant created) {
            this.key = key;
            this.created = created;
        }
    }
}
