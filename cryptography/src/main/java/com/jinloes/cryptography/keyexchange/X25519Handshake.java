package com.jinloes.cryptography.keyexchange;

import com.jinloes.cryptography.kdf.Hkdf;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import javax.crypto.KeyAgreement;

/**
 * Ephemeral X25519 Diffie-Hellman followed by HKDF, the core of TLS 1.3 and Noise handshakes.
 *
 * <p>The raw DH output is not uniformly random, so it is never used directly as a key. It is run
 * through HKDF with a transcript of both public keys as salt, binding the derived key to this exact
 * exchange. Unauthenticated DH is vulnerable to man-in-the-middle; real protocols sign the transcript
 * (TLS server certificate) or use static keys (Noise).
 */
public final class X25519Handshake {
    private static final byte[] INFO = "study-guide x25519 v1 session key".getBytes(StandardCharsets.UTF_8);

    private final KeyPair ephemeral;

    public X25519Handshake() throws GeneralSecurityException {
        this.ephemeral = KeyPairGenerator.getInstance("X25519").generateKeyPair();
    }

    public PublicKey publicKey() {
        return ephemeral.getPublic();
    }

    /**
     * Derives a 32-byte session key. {@code initiatorKey} and {@code responderKey} must be passed in
     * the same order on both sides so the transcripts match.
     */
    public byte[] deriveSessionKey(PublicKey peer, PublicKey initiatorKey, PublicKey responderKey)
            throws GeneralSecurityException {
        KeyAgreement agreement = KeyAgreement.getInstance("X25519");
        agreement.init(ephemeral.getPrivate());
        agreement.doPhase(peer, true);
        byte[] sharedSecret = agreement.generateSecret();

        byte[] a = initiatorKey.getEncoded();
        byte[] b = responderKey.getEncoded();
        byte[] transcript = ByteBuffer.allocate(a.length + b.length).put(a).put(b).array();
        return Hkdf.derive(transcript, sharedSecret, INFO, 32);
    }
}
