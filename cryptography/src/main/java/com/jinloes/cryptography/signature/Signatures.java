package com.jinloes.cryptography.signature;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

/**
 * Digital signatures with the two schemes most worth knowing.
 *
 * <ul>
 *   <li>Ed25519: deterministic, fast, no per-signature randomness to get wrong. Preferred default.</li>
 *   <li>ECDSA P-256: ubiquitous in TLS, X.509 and HSMs. Needs a unique secret nonce per signature;
 *       a repeated or biased nonce leaks the private key (the classic game console and wallet
 *       failures). The JDK derives nonces safely; hand-rolled implementations often do not.</li>
 * </ul>
 */
public final class Signatures {
    public enum Scheme {
        ED25519("Ed25519", "Ed25519"),
        ECDSA_P256("SHA256withECDSA", "EC");

        private final String signatureAlgorithm;
        private final String keyAlgorithm;

        Scheme(String signatureAlgorithm, String keyAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
            this.keyAlgorithm = keyAlgorithm;
        }
    }

    private Signatures() {
    }

    public static KeyPair generateKeyPair(Scheme scheme) throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(scheme.keyAlgorithm);
        if (scheme == Scheme.ECDSA_P256) {
            generator.initialize(new ECGenParameterSpec("secp256r1"));
        }
        return generator.generateKeyPair();
    }

    public static byte[] sign(Scheme scheme, PrivateKey key, byte[] message) throws GeneralSecurityException {
        Signature signer = Signature.getInstance(scheme.signatureAlgorithm);
        signer.initSign(key);
        signer.update(message);
        return signer.sign();
    }

    /** Returns false (never throws) for a bad or malformed signature, so callers cannot fail open. */
    public static boolean verify(Scheme scheme, PublicKey key, byte[] message, byte[] signature) {
        try {
            Signature verifier = Signature.getInstance(scheme.signatureAlgorithm);
            verifier.initVerify(key);
            verifier.update(message);
            return verifier.verify(signature);
        } catch (GeneralSecurityException e) {
            return false;
        }
    }
}
