package com.jinloes.cryptography.kms;

/**
 * A data encryption key (DEK). Use {@code plaintext} in memory to encrypt, persist only
 * {@code wrapped}, and discard the plaintext as soon as possible.
 */
public record DataKey(byte[] plaintext, byte[] wrapped) {
}
