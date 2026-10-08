package com.jinloes.cryptography.kms;

import java.time.Instant;
import java.util.Map;

/** Every key use is logged with who/what/why. Encryption context is safe to log; plaintext never is. */
public record AuditEvent(Instant time, String operation, String keyId, int keyVersion,
                         Map<String, String> encryptionContext, boolean success) {
}
