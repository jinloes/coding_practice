package com.jinloes.cryptography.envelope;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jinloes.cryptography.aead.AesGcm;
import com.jinloes.cryptography.kms.InMemoryKms;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EnvelopeEncryptorTest {
    private static final Map<String, String> CONTEXT = Map.of("tenant", "acme", "record", "invoice-7");

    private final InMemoryKms kms = new InMemoryKms();
    private final String keyId = kms.createKey();
    private final EnvelopeEncryptor encryptor = new EnvelopeEncryptor(kms, keyId);

    @Test
    void encryptsDataLargerThanKmsLimit() throws Exception {
        byte[] large = new byte[1_000_000];
        Arrays.fill(large, (byte) 'x');

        byte[] envelope = encryptor.encrypt(large, CONTEXT);

        assertThat(encryptor.decrypt(envelope, CONTEXT)).isEqualTo(large);
    }

    @Test
    void contextIsBoundToPayload() throws Exception {
        byte[] envelope = encryptor.encrypt("data".getBytes(UTF_8), CONTEXT);

        assertThatThrownBy(() -> encryptor.decrypt(envelope, Map.of("tenant", "evil")))
                .isInstanceOf(GeneralSecurityException.class);
    }

    @Test
    void rewrapMovesDataKeyToNewKekVersionWithoutTouchingPayload() throws Exception {
        byte[] plaintext = "customer record".getBytes(UTF_8);
        byte[] original = encryptor.encrypt(plaintext, CONTEXT);
        kms.rotateKey(keyId);

        byte[] rewrapped = encryptor.rewrap(original, CONTEXT);
        kms.disableVersion(keyId, 1);

        assertThat(encryptor.decrypt(rewrapped, CONTEXT)).isEqualTo(plaintext);
        assertThatThrownBy(() -> encryptor.decrypt(original, CONTEXT)).isInstanceOf(GeneralSecurityException.class);
        int payloadLength = AesGcm.NONCE_BYTES + plaintext.length + AesGcm.TAG_BYTES;
        assertThat(Arrays.copyOfRange(rewrapped, rewrapped.length - payloadLength, rewrapped.length))
                .isEqualTo(Arrays.copyOfRange(original, original.length - payloadLength, original.length));
    }

    @Test
    void rejectsMalformedEnvelope() {
        assertThatThrownBy(() -> encryptor.decrypt(new byte[] {1, 0x7f, 0, 0, 0}, CONTEXT))
                .isInstanceOf(GeneralSecurityException.class);
        assertThatThrownBy(() -> encryptor.decrypt(new byte[] {9}, CONTEXT))
                .isInstanceOf(GeneralSecurityException.class);
    }
}
