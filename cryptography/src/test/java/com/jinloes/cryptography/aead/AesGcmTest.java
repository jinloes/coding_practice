package com.jinloes.cryptography.aead;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.crypto.AEADBadTagException;
import org.junit.jupiter.api.Test;

class AesGcmTest {
    private final AesGcm aead = new AesGcm(AesGcm.generateKey());
    private final byte[] plaintext = "member email".getBytes(UTF_8);
    private final byte[] aad = "table=users,row=42".getBytes(UTF_8);

    @Test
    void roundTrips() throws Exception {
        byte[] ciphertext = aead.encrypt(plaintext, aad);

        assertThat(ciphertext).hasSize(AesGcm.NONCE_BYTES + plaintext.length + AesGcm.TAG_BYTES);
        assertThat(aead.decrypt(ciphertext, aad)).isEqualTo(plaintext);
    }

    @Test
    void encryptionIsRandomized() throws Exception {
        assertThat(aead.encrypt(plaintext, aad)).isNotEqualTo(aead.encrypt(plaintext, aad));
    }

    @Test
    void detectsTamperedCiphertext() throws Exception {
        byte[] ciphertext = aead.encrypt(plaintext, aad);
        ciphertext[AesGcm.NONCE_BYTES] ^= 1;

        assertThatThrownBy(() -> aead.decrypt(ciphertext, aad)).isInstanceOf(AEADBadTagException.class);
    }

    @Test
    void detectsWrongAssociatedData() throws Exception {
        byte[] ciphertext = aead.encrypt(plaintext, aad);

        assertThatThrownBy(() -> aead.decrypt(ciphertext, "table=users,row=43".getBytes(UTF_8)))
                .isInstanceOf(AEADBadTagException.class);
    }

    @Test
    void rejectsWrongKey() throws Exception {
        byte[] ciphertext = aead.encrypt(plaintext, aad);

        assertThatThrownBy(() -> new AesGcm(AesGcm.generateKey()).decrypt(ciphertext, aad))
                .isInstanceOf(AEADBadTagException.class);
    }

    @Test
    void rejectsTruncatedInputAndBadKeySizes() {
        assertThatThrownBy(() -> aead.decrypt(new byte[10], aad)).isInstanceOf(AEADBadTagException.class);
        assertThatThrownBy(() -> new AesGcm(new byte[20])).isInstanceOf(IllegalArgumentException.class);
    }
}
