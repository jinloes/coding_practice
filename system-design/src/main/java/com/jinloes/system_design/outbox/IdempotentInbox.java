package com.jinloes.system_design.outbox;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Scoped idempotency store / consumer inbox that deduplicates retries while rejecting accidental
 * key reuse across mutated payloads.
 *
 * <p>See {@code docs/patterns/idempotency.md} and {@code docs/case-studies/payment-processing.md}.
 */
public final class IdempotentInbox<R> {
    public static final class IdempotencyConflictException extends RuntimeException {
        public IdempotencyConflictException(String scope, String idempotencyKey) {
            super("Idempotency key '" + idempotencyKey + "' in scope '" + scope
                + "' was reused with a different payload");
        }
    }

    public record Outcome<R>(R value, boolean replayed) {
    }

    private final Map<ScopedKey, StoredRecord<R>> records = new ConcurrentHashMap<>();

    public Outcome<R> executeOnce(
        String scope,
        String idempotencyKey,
        String canonicalPayload,
        Supplier<R> effect
    ) {
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
        Objects.requireNonNull(canonicalPayload, "canonicalPayload must not be null");
        Objects.requireNonNull(effect, "effect must not be null");

        String payloadHash = sha256Hex(canonicalPayload);
        ScopedKey key = new ScopedKey(scope, idempotencyKey);

        boolean[] executedNow = {false};
        StoredRecord<R> stored = records.compute(key, (ignored, existing) -> {
            if (existing != null) {
                if (!existing.payloadHash().equals(payloadHash)) {
                    throw new IdempotencyConflictException(scope, idempotencyKey);
                }
                return existing;
            }
            executedNow[0] = true;
            return new StoredRecord<>(payloadHash, effect.get());
        });
        return new Outcome<>(stored.result(), !executedNow[0]);
    }

    private static String sha256Hex(String payload) {
        return HexFormat.of().formatHex(digest("SHA-256").digest(payload.getBytes(StandardCharsets.UTF_8)));
    }

    static MessageDigest digest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algorithm + " is required by the Java platform", e);
        }
    }

    private record ScopedKey(String scope, String idempotencyKey) {
    }

    private record StoredRecord<R>(String payloadHash, R result) {
    }
}
