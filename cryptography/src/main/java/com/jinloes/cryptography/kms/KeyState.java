package com.jinloes.cryptography.kms;

/** Lifecycle of a single key version, loosely following NIST SP 800-57. */
public enum KeyState {
    /** Can encrypt (if primary) and decrypt. */
    ENABLED,
    /** Reversible kill switch: no operations, material retained. */
    DISABLED,
    /** Material erased. Anything encrypted under this version is permanently unreadable. */
    DESTROYED
}
