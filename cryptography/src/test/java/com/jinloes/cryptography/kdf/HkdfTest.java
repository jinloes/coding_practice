package com.jinloes.cryptography.kdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class HkdfTest {
    private static final HexFormat HEX = HexFormat.of();
    private static final byte[] IKM = HEX.parseHex("0b".repeat(22));

    @Test
    void rfc5869TestCase1() throws Exception {
        byte[] salt = HEX.parseHex("000102030405060708090a0b0c");
        byte[] info = HEX.parseHex("f0f1f2f3f4f5f6f7f8f9");

        byte[] prk = Hkdf.extract(salt, IKM);
        assertThat(HEX.formatHex(prk))
                .isEqualTo("077709362c2e32df0ddc3f0dc47bba6390b6c73bb50f9c3122ec844ad7c2b3e5");
        assertThat(HEX.formatHex(Hkdf.expand(prk, info, 42))).isEqualTo(
                "3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865");
    }

    @Test
    void rfc5869TestCase3EmptySaltAndInfo() throws Exception {
        assertThat(HEX.formatHex(Hkdf.derive(new byte[0], IKM, new byte[0], 42))).isEqualTo(
                "8da4e775a563c18f715f802a063c5a31b8a11f5c5ee1879ec3454e5f3c738d2d9d201395faa4b61a96c8");
    }

    @Test
    void differentInfoGivesIndependentKeys() throws Exception {
        byte[] encryptionKey = Hkdf.derive(null, IKM, "encrypt".getBytes(), 32);
        byte[] macKey = Hkdf.derive(null, IKM, "mac".getBytes(), 32);

        assertThat(encryptionKey).isNotEqualTo(macKey);
    }

    @Test
    void rejectsOutOfRangeLength() {
        assertThatThrownBy(() -> Hkdf.expand(new byte[32], null, 255 * 32 + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
