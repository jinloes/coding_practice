package com.jinloes.cryptography.keyexchange;

import static org.assertj.core.api.Assertions.assertThat;

import com.jinloes.cryptography.aead.AesGcm;
import org.junit.jupiter.api.Test;

class X25519HandshakeTest {

    @Test
    void bothSidesDeriveSameKeyAndCanTalk() throws Exception {
        X25519Handshake alice = new X25519Handshake();
        X25519Handshake bob = new X25519Handshake();

        byte[] aliceKey = alice.deriveSessionKey(bob.publicKey(), alice.publicKey(), bob.publicKey());
        byte[] bobKey = bob.deriveSessionKey(alice.publicKey(), alice.publicKey(), bob.publicKey());

        assertThat(aliceKey).isEqualTo(bobKey).hasSize(32);
        byte[] ciphertext = new AesGcm(aliceKey).encrypt("hi bob".getBytes(), null);
        assertThat(new AesGcm(bobKey).decrypt(ciphertext, null)).isEqualTo("hi bob".getBytes());
    }

    @Test
    void eavesdropperWithOwnKeyDerivesDifferentKey() throws Exception {
        X25519Handshake alice = new X25519Handshake();
        X25519Handshake bob = new X25519Handshake();
        X25519Handshake eve = new X25519Handshake();

        byte[] aliceKey = alice.deriveSessionKey(bob.publicKey(), alice.publicKey(), bob.publicKey());
        byte[] eveKey = eve.deriveSessionKey(bob.publicKey(), alice.publicKey(), bob.publicKey());

        assertThat(eveKey).isNotEqualTo(aliceKey);
    }

    @Test
    void ephemeralKeysGiveFreshSessionKeys() throws Exception {
        X25519Handshake bob = new X25519Handshake();
        X25519Handshake first = new X25519Handshake();
        X25519Handshake second = new X25519Handshake();

        byte[] k1 = first.deriveSessionKey(bob.publicKey(), first.publicKey(), bob.publicKey());
        byte[] k2 = second.deriveSessionKey(bob.publicKey(), second.publicKey(), bob.publicKey());

        assertThat(k1).isNotEqualTo(k2);
    }
}
