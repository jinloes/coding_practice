package com.jinloes.cryptography.aead;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Misuse-resistant wrapper around AES-GCM.
 *
 * <p>Output format: {@code nonce (12 bytes) || ciphertext || tag (16 bytes)}. The caller never chooses
 * a nonce, so nonce reuse (catastrophic for GCM) cannot happen through this API. With random 96-bit
 * nonces, NIST SP 800-38D limits a single key to about 2^32 encryptions; rotate keys well before that.
 */
public final class AesGcm {
    public static final int NONCE_BYTES = 12;
    public static final int TAG_BYTES = 16;
    private static final int TAG_BITS = TAG_BYTES * 8;
    private static final byte[] EMPTY = new byte[0];
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKey key;

    public AesGcm(byte[] key) {
        if (key.length != 16 && key.length != 32) {
            throw new IllegalArgumentException("AES key must be 16 or 32 bytes, got " + key.length);
        }
        this.key = new SecretKeySpec(key.clone(), "AES");
    }

    /** Generates a fresh random 256-bit key. */
    public static byte[] generateKey() {
        byte[] key = new byte[32];
        RANDOM.nextBytes(key);
        return key;
    }

    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        byte[] nonce = new byte[NONCE_BYTES];
        RANDOM.nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, nonce));
        cipher.updateAAD(orEmpty(associatedData));
        byte[] ciphertext = cipher.doFinal(plaintext);
        return ByteBuffer.allocate(NONCE_BYTES + ciphertext.length).put(nonce).put(ciphertext).array();
    }

    /**
     * Decrypts and authenticates. Throws {@link AEADBadTagException} if the ciphertext, nonce, or
     * associated data were modified, or if the wrong key was used. No plaintext is ever released on
     * failure.
     */
    public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        if (ciphertext.length < NONCE_BYTES + TAG_BYTES) {
            throw new AEADBadTagException("ciphertext too short");
        }
        byte[] nonce = Arrays.copyOfRange(ciphertext, 0, NONCE_BYTES);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, nonce));
        cipher.updateAAD(orEmpty(associatedData));
        return cipher.doFinal(ciphertext, NONCE_BYTES, ciphertext.length - NONCE_BYTES);
    }

    private static byte[] orEmpty(byte[] bytes) {
        return bytes == null ? EMPTY : bytes;
    }
}
