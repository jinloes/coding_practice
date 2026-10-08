package com.jinloes.cryptography.kms;

import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Minimal KMS API modeled on the operations every cloud KMS exposes.
 *
 * <p>The key material behind a {@code keyId} never leaves the service. Callers only ever see
 * ciphertext blobs, which are self-describing: they carry the key id and key version needed to
 * decrypt, so decrypt does not take a key id and keeps working after rotation.
 *
 * <p>The encryption context is non-secret, authenticated metadata (tenant, purpose, record id). The
 * same context must be supplied to decrypt; it also gives auditors a meaningful record of why a key
 * was used.
 */
public interface KeyManagementService {

    /** Creates a new key (version 1, primary) and returns its id. */
    String createKey();

    /** Adds a new primary version. Older versions keep decrypting; only the primary encrypts. */
    int rotateKey(String keyId);

    /** Directly encrypts a small secret (at most {@link #MAX_PLAINTEXT_BYTES}). */
    byte[] encrypt(String keyId, byte[] plaintext, Map<String, String> encryptionContext)
            throws GeneralSecurityException;

    byte[] decrypt(byte[] ciphertextBlob, Map<String, String> encryptionContext) throws GeneralSecurityException;

    /** Returns a fresh 256-bit data key in plaintext and wrapped (encrypted under {@code keyId}) form. */
    DataKey generateDataKey(String keyId, Map<String, String> encryptionContext) throws GeneralSecurityException;

    /** KMS is for keys, not bulk data. Bulk data uses envelope encryption. */
    int MAX_PLAINTEXT_BYTES = 4096;
}
