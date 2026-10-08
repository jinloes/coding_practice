package com.jinloes.cryptography.kdf;

import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HKDF-SHA256 from RFC 5869, implemented on top of HMAC as a study exercise.
 *
 * <p>Extract concentrates the entropy of input keying material into a pseudorandom key (PRK).
 * Expand stretches the PRK into any number of independent output keys, separated by {@code info}.
 * Use HKDF for high-entropy inputs (DH shared secrets, master keys). Never use it for passwords; use
 * a slow, salted password hash instead.
 */
public final class Hkdf {
    private static final String HMAC = "HmacSHA256";
    public static final int HASH_LEN = 32;

    private Hkdf() {
    }

    public static byte[] extract(byte[] salt, byte[] inputKeyMaterial) throws GeneralSecurityException {
        byte[] effectiveSalt = (salt == null || salt.length == 0) ? new byte[HASH_LEN] : salt;
        return hmac(effectiveSalt).doFinal(inputKeyMaterial);
    }

    public static byte[] expand(byte[] prk, byte[] info, int length) throws GeneralSecurityException {
        if (length <= 0 || length > 255 * HASH_LEN) {
            throw new IllegalArgumentException("length must be in [1, " + 255 * HASH_LEN + "]");
        }
        Mac mac = hmac(prk);
        ByteArrayOutputStream okm = new ByteArrayOutputStream(length);
        byte[] previous = new byte[0];
        for (int counter = 1; okm.size() < length; counter++) {
            mac.update(previous);
            if (info != null) {
                mac.update(info);
            }
            mac.update((byte) counter);
            previous = mac.doFinal();
            okm.write(previous, 0, Math.min(previous.length, length - okm.size()));
        }
        return okm.toByteArray();
    }

    public static byte[] derive(byte[] salt, byte[] inputKeyMaterial, byte[] info, int length)
            throws GeneralSecurityException {
        return expand(extract(salt, inputKeyMaterial), info, length);
    }

    private static Mac hmac(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC);
        mac.init(new SecretKeySpec(key, HMAC));
        return mac;
    }
}
