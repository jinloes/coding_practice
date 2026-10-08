package com.jinloes.cryptography.envelope;

import com.jinloes.cryptography.aead.AesGcm;
import com.jinloes.cryptography.kms.DataKey;
import com.jinloes.cryptography.kms.EncryptionContext;
import com.jinloes.cryptography.kms.KeyManagementService;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

/**
 * Envelope encryption: bulk data is encrypted locally with a data key (DEK); only the small DEK is
 * sent to KMS to be wrapped by a key encryption key (KEK) that never leaves KMS.
 *
 * <p>Envelope format:
 * <pre>
 *   byte  formatVersion (1)
 *   int   wrappedKeyLength, then wrapped DEK (a KMS ciphertext blob)
 *   ...   AES-256-GCM payload (nonce || ciphertext || tag)
 * </pre>
 * The payload's associated data is {@code formatVersion || canonical(context)}. It deliberately
 * excludes the wrapped key bytes so {@link #rewrap} can replace them without touching the payload.
 */
public final class EnvelopeEncryptor {
    private static final byte FORMAT_VERSION = 1;

    private final KeyManagementService kms;
    private final DataKeySource dataKeys;
    private final String keyId;

    public EnvelopeEncryptor(KeyManagementService kms, String keyId) {
        this(kms, kms::generateDataKey, keyId);
    }

    public EnvelopeEncryptor(KeyManagementService kms, DataKeySource dataKeys, String keyId) {
        this.kms = kms;
        this.dataKeys = dataKeys;
        this.keyId = keyId;
    }

    public byte[] encrypt(byte[] plaintext, Map<String, String> context) throws GeneralSecurityException {
        DataKey dataKey = dataKeys.dataKey(keyId, context);
        try {
            byte[] payload = new AesGcm(dataKey.plaintext()).encrypt(plaintext, payloadAad(context));
            return assemble(dataKey.wrapped(), payload);
        } finally {
            Arrays.fill(dataKey.plaintext(), (byte) 0);
        }
    }

    public byte[] decrypt(byte[] envelope, Map<String, String> context) throws GeneralSecurityException {
        Parsed parsed = parse(envelope);
        byte[] dek = kms.decrypt(parsed.wrappedKey, context);
        try {
            return new AesGcm(dek).decrypt(parsed.payload, payloadAad(context));
        } finally {
            Arrays.fill(dek, (byte) 0);
        }
    }

    /**
     * Re-wraps the DEK under the KEK's current primary version without re-encrypting the payload.
     * This is how stored data migrates off an old KEK version cheaply after rotation. (A real KMS
     * offers a server-side ReEncrypt so the DEK plaintext never reaches the caller.)
     */
    public byte[] rewrap(byte[] envelope, Map<String, String> context) throws GeneralSecurityException {
        Parsed parsed = parse(envelope);
        byte[] dek = kms.decrypt(parsed.wrappedKey, context);
        try {
            return assemble(kms.encrypt(keyId, dek, context), parsed.payload);
        } finally {
            Arrays.fill(dek, (byte) 0);
        }
    }

    private static byte[] assemble(byte[] wrappedKey, byte[] payload) {
        return ByteBuffer.allocate(1 + 4 + wrappedKey.length + payload.length)
                .put(FORMAT_VERSION).putInt(wrappedKey.length).put(wrappedKey).put(payload).array();
    }

    private static Parsed parse(byte[] envelope) throws GeneralSecurityException {
        try {
            ByteBuffer in = ByteBuffer.wrap(envelope);
            if (in.get() != FORMAT_VERSION) {
                throw new GeneralSecurityException("unsupported envelope format");
            }
            int wrappedLength = in.getInt();
            // Validate attacker-controlled lengths before allocating, or a 4-byte header can force a 2 GB allocation.
            if (wrappedLength < 0 || wrappedLength > in.remaining()) {
                throw new GeneralSecurityException("malformed envelope");
            }
            byte[] wrapped = new byte[wrappedLength];
            in.get(wrapped);
            byte[] payload = new byte[in.remaining()];
            in.get(payload);
            return new Parsed(wrapped, payload);
        } catch (BufferUnderflowException e) {
            throw new GeneralSecurityException("malformed envelope");
        }
    }

    private static byte[] payloadAad(Map<String, String> context) {
        byte[] ctx = EncryptionContext.canonicalize(context);
        return ByteBuffer.allocate(1 + ctx.length).put(FORMAT_VERSION).put(ctx).array();
    }

    private record Parsed(byte[] wrappedKey, byte[] payload) {
    }
}
