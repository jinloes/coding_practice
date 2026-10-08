package com.jinloes.cryptography.password;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {
    // Low iteration count keeps the test fast; production uses RECOMMENDED_ITERATIONS.
    private final PasswordHasher hasher = new PasswordHasher(1_000);

    @Test
    void verifiesCorrectPasswordOnly() throws Exception {
        String stored = hasher.hash("correct horse".toCharArray());

        assertThat(stored).startsWith("pbkdf2-sha256$1000$");
        assertThat(hasher.verify("correct horse".toCharArray(), stored)).isTrue();
        assertThat(hasher.verify("correct horsf".toCharArray(), stored)).isFalse();
    }

    @Test
    void saltMakesEqualPasswordsHashDifferently() throws Exception {
        assertThat(hasher.hash("same".toCharArray())).isNotEqualTo(hasher.hash("same".toCharArray()));
    }

    @Test
    void flagsWeakParametersForRehash() throws Exception {
        String weak = new PasswordHasher(10).hash("pw".toCharArray());

        assertThat(hasher.needsRehash(weak)).isTrue();
        assertThat(hasher.needsRehash(hasher.hash("pw".toCharArray()))).isFalse();
        assertThat(hasher.verify("pw".toCharArray(), "md5$deadbeef")).isFalse();
    }
}
