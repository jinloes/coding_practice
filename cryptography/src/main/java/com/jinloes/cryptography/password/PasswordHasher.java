package com.jinloes.cryptography.password;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Salted, slow password hashing with PBKDF2-HMAC-SHA256 (the JDK's only built-in option).
 *
 * <p>Prefer Argon2id or scrypt when a vetted library is available; they are memory-hard and resist
 * GPU/ASIC cracking better. OWASP recommends at least 600,000 PBKDF2-SHA256 iterations.
 *
 * <p>Encoded format: {@code pbkdf2-sha256$iterations$base64(salt)$base64(hash)}. Storing parameters
 * with the hash lets iteration counts rise over time and upgrade on next login.
 */
public final class PasswordHasher {
    public static final int RECOMMENDED_ITERATIONS = 600_000;
    private static final String PREFIX = "pbkdf2-sha256";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final int iterations;

    public PasswordHasher(int iterations) {
        if (iterations < 1) {
            throw new IllegalArgumentException("iterations must be positive");
        }
        this.iterations = iterations;
    }

    public String hash(char[] password) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, iterations);
        Base64.Encoder b64 = Base64.getEncoder();
        return String.join("$", PREFIX, Integer.toString(iterations), b64.encodeToString(salt), b64.encodeToString(hash));
    }

    public boolean verify(char[] password, String encoded) throws GeneralSecurityException {
        String[] parts = encoded.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] expected = b64.decode(parts[3]);
        byte[] actual = pbkdf2(password, b64.decode(parts[2]), Integer.parseInt(parts[1]));
        // Constant-time comparison: a short-circuiting equals leaks how many leading bytes matched.
        return MessageDigest.isEqual(expected, actual);
    }

    /** True when the stored hash used weaker parameters and should be re-hashed after a successful login. */
    public boolean needsRehash(String encoded) {
        String[] parts = encoded.split("\\$");
        return parts.length != 4 || !PREFIX.equals(parts[0]) || Integer.parseInt(parts[1]) < iterations;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
