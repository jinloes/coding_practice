package com.jinloes.cryptography.signature;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.cryptography.signature.Signatures.Scheme;
import java.security.KeyPair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SignaturesTest {
    private final byte[] message = "release-artifact-sha256:abc123".getBytes(UTF_8);

    @ParameterizedTest
    @EnumSource(Scheme.class)
    void signAndVerify(Scheme scheme) throws Exception {
        KeyPair keys = Signatures.generateKeyPair(scheme);
        byte[] signature = Signatures.sign(scheme, keys.getPrivate(), message);

        assertThat(Signatures.verify(scheme, keys.getPublic(), message, signature)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Scheme.class)
    void rejectsTamperedMessageWrongKeyAndGarbage(Scheme scheme) throws Exception {
        KeyPair keys = Signatures.generateKeyPair(scheme);
        byte[] signature = Signatures.sign(scheme, keys.getPrivate(), message);
        byte[] tampered = message.clone();
        tampered[0] ^= 1;

        assertThat(Signatures.verify(scheme, keys.getPublic(), tampered, signature)).isFalse();
        assertThat(Signatures.verify(scheme, Signatures.generateKeyPair(scheme).getPublic(), message, signature)).isFalse();
        assertThat(Signatures.verify(scheme, keys.getPublic(), message, new byte[] {1, 2, 3})).isFalse();
    }

    @ParameterizedTest
    @EnumSource(Scheme.class)
    void ed25519IsDeterministicEcdsaIsNot(Scheme scheme) throws Exception {
        KeyPair keys = Signatures.generateKeyPair(scheme);
        byte[] first = Signatures.sign(scheme, keys.getPrivate(), message);
        byte[] second = Signatures.sign(scheme, keys.getPrivate(), message);

        if (scheme == Scheme.ED25519) {
            assertThat(first).isEqualTo(second);
        } else {
            assertThat(first).isNotEqualTo(second);
        }
    }
}
