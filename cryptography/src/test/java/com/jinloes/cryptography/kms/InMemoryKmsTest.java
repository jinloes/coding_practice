package com.jinloes.cryptography.kms;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class InMemoryKmsTest {
    private static final Map<String, String> CONTEXT = Map.of("tenant", "acme", "purpose", "db-password");

    private final InMemoryKms kms = new InMemoryKms();
    private final String keyId = kms.createKey();
    private final byte[] secret = "hunter2".getBytes(UTF_8);

    @Test
    void encryptDecryptWithContext() throws Exception {
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);

        assertThat(kms.decrypt(blob, CONTEXT)).isEqualTo(secret);
    }

    @Test
    void contextMustMatchButOrderDoesNot() throws Exception {
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);
        Map<String, String> reordered = new LinkedHashMap<>();
        reordered.put("purpose", "db-password");
        reordered.put("tenant", "acme");

        assertThat(kms.decrypt(blob, reordered)).isEqualTo(secret);
        assertThatThrownBy(() -> kms.decrypt(blob, Map.of("tenant", "other", "purpose", "db-password")))
                .isInstanceOf(GeneralSecurityException.class);
    }

    @Test
    void oldCiphertextStillDecryptsAfterRotation() throws Exception {
        byte[] beforeRotation = kms.encrypt(keyId, secret, CONTEXT);

        assertThat(kms.rotateKey(keyId)).isEqualTo(2);
        assertThat(kms.primaryVersion(keyId)).isEqualTo(2);
        assertThat(kms.decrypt(beforeRotation, CONTEXT)).isEqualTo(secret);
        assertThat(kms.decrypt(kms.encrypt(keyId, secret, CONTEXT), CONTEXT)).isEqualTo(secret);
    }

    @Test
    void disableIsReversible() throws Exception {
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);
        kms.disableVersion(keyId, 1);

        assertThatThrownBy(() -> kms.decrypt(blob, CONTEXT)).hasMessageContaining("DISABLED");

        kms.enableVersion(keyId, 1);
        assertThat(kms.decrypt(blob, CONTEXT)).isEqualTo(secret);
    }

    @Test
    void destroyIsPermanentCryptoShredding() throws Exception {
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);
        kms.rotateKey(keyId);
        kms.destroyVersion(keyId, 1);

        assertThat(kms.state(keyId, 1)).isEqualTo(KeyState.DESTROYED);
        assertThatThrownBy(() -> kms.decrypt(blob, CONTEXT)).hasMessageContaining("DESTROYED");
        assertThatThrownBy(() -> kms.enableVersion(keyId, 1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void tamperedHeaderIsRejected() throws Exception {
        String otherKey = kms.createKey();
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);
        byte[] spliced = blob.clone();
        // Swap in the other key id (same length UUID) so the blob claims to belong to otherKey.
        System.arraycopy(otherKey.getBytes(UTF_8), 0, spliced, 3, otherKey.length());

        assertThatThrownBy(() -> kms.decrypt(spliced, CONTEXT)).isInstanceOf(GeneralSecurityException.class);
    }

    @Test
    void rejectsMalformedAndOversizedInput() throws Exception {
        byte[] withoutContext = kms.encrypt(keyId, secret, null);
        assertThat(kms.decrypt(withoutContext, null)).isEqualTo(secret);

        assertThatThrownBy(() -> kms.decrypt(new byte[] {99}, CONTEXT)).isInstanceOf(GeneralSecurityException.class);
        assertThatThrownBy(() -> kms.decrypt(new byte[] {1, 0}, CONTEXT)).isInstanceOf(GeneralSecurityException.class);
        assertThatThrownBy(() -> kms.decrypt(new byte[] {1, (byte) 0xff, (byte) 0xff}, CONTEXT))
                .isInstanceOf(GeneralSecurityException.class);
        assertThatThrownBy(() -> kms.decrypt(new byte[] {1, 0, 10, 0}, CONTEXT))
                .isInstanceOf(GeneralSecurityException.class);

        InMemoryKms otherKms = new InMemoryKms();
        String foreignKey = otherKms.createKey();
        byte[] foreignBlob = otherKms.encrypt(foreignKey, secret, CONTEXT);
        assertThatThrownBy(() -> kms.decrypt(foreignBlob, CONTEXT)).isInstanceOf(GeneralSecurityException.class);

        int versionOffset = 1 + Short.BYTES + keyId.getBytes(UTF_8).length;
        byte[] zeroVersionBlob = withoutContext.clone();
        java.nio.ByteBuffer.wrap(zeroVersionBlob, versionOffset, Integer.BYTES).putInt(0);
        assertThatThrownBy(() -> kms.decrypt(zeroVersionBlob, null)).isInstanceOf(GeneralSecurityException.class);

        byte[] unknownVersionBlob = withoutContext.clone();
        java.nio.ByteBuffer.wrap(unknownVersionBlob, versionOffset, Integer.BYTES).putInt(99);
        assertThatThrownBy(() -> kms.decrypt(unknownVersionBlob, null)).isInstanceOf(GeneralSecurityException.class);

        assertThatThrownBy(() -> kms.rotateKey("missing-key")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> kms.state(keyId, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> kms.state(keyId, 99)).isInstanceOf(IllegalArgumentException.class);

        kms.disableVersion(keyId, 1);
        assertThatThrownBy(() -> kms.primaryVersion(keyId)).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> kms.encrypt(keyId, new byte[KeyManagementService.MAX_PLAINTEXT_BYTES + 1], CONTEXT))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void generatesUsableDataKeys() throws Exception {
        DataKey dataKey = kms.generateDataKey(keyId, CONTEXT);

        assertThat(dataKey.plaintext()).hasSize(32);
        assertThat(kms.decrypt(dataKey.wrapped(), CONTEXT)).isEqualTo(dataKey.plaintext());
    }

    @Test
    void auditsSuccessesAndFailures() throws Exception {
        byte[] blob = kms.encrypt(keyId, secret, CONTEXT);
        kms.decrypt(blob, CONTEXT);
        try {
            kms.decrypt(blob, Map.of());
        } catch (GeneralSecurityException expected) {
            // recorded below
        }

        assertThat(kms.auditLog())
                .extracting(AuditEvent::operation, AuditEvent::success)
                .containsExactly(
                        tuple("CreateKey", true),
                        tuple("Encrypt", true),
                        tuple("Decrypt", true),
                        tuple("Decrypt", false));
    }
}
