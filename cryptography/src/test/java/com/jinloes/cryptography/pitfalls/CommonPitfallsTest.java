package com.jinloes.cryptography.pitfalls;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.cryptography.aead.AesGcm;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

/** Executable demonstrations of why the safe APIs in this module look the way they do. */
class CommonPitfallsTest {
    private final SecretKeySpec key = new SecretKeySpec(AesGcm.generateKey(), "AES");

    @Test
    void ecbLeaksPatterns() throws Exception {
        byte[] twoEqualBlocks = "YELLOW SUBMARINEYELLOW SUBMARINE".getBytes(UTF_8);
        Cipher ecb = Cipher.getInstance("AES/ECB/NoPadding");
        ecb.init(Cipher.ENCRYPT_MODE, key);

        byte[] ciphertext = ecb.doFinal(twoEqualBlocks);

        assertThat(Arrays.copyOfRange(ciphertext, 0, 16)).isEqualTo(Arrays.copyOfRange(ciphertext, 16, 32));
    }

    @Test
    void nonceReuseInCounterModeLeaksXorOfPlaintexts() throws Exception {
        // GCM is CTR mode plus a MAC, so the same leak applies to GCM, and GCM nonce reuse also lets an
        // attacker recover the authentication key and forge messages.
        byte[] p1 = "attack at dawn!!".getBytes(UTF_8);
        byte[] p2 = "retreat at dusk!".getBytes(UTF_8);
        IvParameterSpec reusedIv = new IvParameterSpec(new byte[16]);
        Cipher ctr = Cipher.getInstance("AES/CTR/NoPadding");

        ctr.init(Cipher.ENCRYPT_MODE, key, reusedIv);
        byte[] c1 = ctr.doFinal(p1);
        ctr.init(Cipher.ENCRYPT_MODE, key, reusedIv);
        byte[] c2 = ctr.doFinal(p2);

        assertThat(xor(c1, c2)).isEqualTo(xor(p1, p2));
        // Knowing (or guessing) one plaintext reveals the other without the key.
        assertThat(xor(xor(c1, c2), p1)).isEqualTo(p2);
    }

    @Test
    void unauthenticatedEncryptionIsMalleable() throws Exception {
        byte[] plaintext = "pay $100 to bob ".getBytes(UTF_8);
        Cipher ctr = Cipher.getInstance("AES/CTR/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        ctr.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] ciphertext = ctr.doFinal(plaintext);

        // Without knowing the key, flip bits so "1" becomes "9".
        ciphertext[5] ^= (byte) ('1' ^ '9');
        ctr.init(Cipher.DECRYPT_MODE, key, iv);

        assertThat(new String(ctr.doFinal(ciphertext), UTF_8)).isEqualTo("pay $900 to bob ");
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }
}
