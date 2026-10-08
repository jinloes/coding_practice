package com.jinloes.cryptography.secretsharing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jinloes.cryptography.secretsharing.ShamirSecretSharing.Share;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShamirSecretSharingTest {
    private final byte[] rootKey = "a 32 byte root key for unsealing".getBytes();

    @Test
    void anyThresholdSubsetRecoversSecret() {
        List<Share> shares = ShamirSecretSharing.split(rootKey, 5, 3);

        assertThat(ShamirSecretSharing.combine(List.of(shares.get(0), shares.get(1), shares.get(2)))).isEqualTo(rootKey);
        assertThat(ShamirSecretSharing.combine(List.of(shares.get(4), shares.get(2), shares.get(0)))).isEqualTo(rootKey);
        assertThat(ShamirSecretSharing.combine(shares)).isEqualTo(rootKey);
    }

    @Test
    void belowThresholdDoesNotRecoverSecret() {
        List<Share> shares = ShamirSecretSharing.split(rootKey, 5, 3);

        assertThat(ShamirSecretSharing.combine(shares.subList(0, 2))).isNotEqualTo(rootKey);
    }

    @Test
    void fieldArithmeticIsConsistent() {
        for (int a = 1; a < 256; a++) {
            assertThat(ShamirSecretSharing.mul(ShamirSecretSharing.div(1, a), a)).isEqualTo(1);
        }
        assertThat(ShamirSecretSharing.mul(0x57, 0x83)).isEqualTo(0xc1); // FIPS-197 example
    }

    @Test
    void validatesArguments() {
        assertThatThrownBy(() -> ShamirSecretSharing.split(rootKey, 3, 4)).isInstanceOf(IllegalArgumentException.class);
        Share share = ShamirSecretSharing.split(rootKey, 3, 2).get(0);
        assertThatThrownBy(() -> ShamirSecretSharing.combine(List.of(share, share)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
