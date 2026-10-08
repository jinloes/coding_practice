# 10 Hands-On Exercises

Each exercise extends the code in this module. Put new code under
`src/main/java/com/jinloes/cryptography/...` with tests (JUnit 5 + AssertJ, package-private test classes),
and run `./gradlew :cryptography:test`.

## 1. TLS inspection (week 1)

Run `openssl s_client -connect example.com:443 -servername example.com -showcerts` and
`curl -v https://example.com`. Identify the protocol version, cipher suite, key exchange group,
chain, SANs, validity, and OCSP stapling. Explain each in one sentence.

## 2. Build a tiny CA (week 1)

With `openssl` (or `step` CLI), create a root CA, an intermediate with `pathlen:0`, and a leaf for
`localhost` with SAN. Verify the chain with `openssl verify -CAfile root.pem -untrusted int.pem leaf.pem`.
Then stand up mTLS between `openssl s_server` and `s_client` with client certs.

## 3. Hash-chained audit log (week 2)

Implement `audit/HashChainedLog`: each entry stores `prevHash` and `hash = SHA-256(prevHash ‖ entry)`.
Add `verify()` that detects modification, deletion, and reordering. Stretch: sign the head every N
entries with `Signatures` (Ed25519) and verify checkpoints.

## 4. Merkle tree with inclusion proofs (week 2)

Implement an RFC 6962-style Merkle tree (leaf prefix `0x00`, node prefix `0x01` to prevent second
preimage attacks). Produce and verify inclusion proofs. Discuss how transparency logs use this.

## 5. Kubernetes secrets encryption lab (week 2)

On `kind` or `minikube`, configure `EncryptionConfiguration` with `aescbc` or `secretbox`, create a Secret,
and confirm via `etcdctl get` that it's encrypted at rest. Read about the KMS v2 plugin and sketch
how you'd implement one against a real KMS. Also try workload identity or a projected service account
token and decode its claims.

## 6. AES Key Wrap (week 3)

Implement RFC 3394 AES-KW (or use `Cipher.getInstance("AESWrap")`) and test with the RFC's vectors.
Compare it with wrapping via AES-GCM: deterministic, no nonce, fixed 8-byte integrity check.
Where is each used (PKCS#11, JWE `A256KW`, cloud KMS import)?

## 7. Streaming AEAD (week 3)

Implement segment-based streaming encryption: header = random nonce prefix + salt, per-segment nonce =
prefix ‖ segment counter ‖ last-segment flag, and AES-GCM per segment. Tests should cover reordering,
truncation (dropping the final segment), and segment duplication; all must fail.

## 8. Keyset with primary and key-id prefix (week 3)

Build a `Keyset` of AES-GCM keys with a primary. Output = `0x01 ‖ keyId(4 bytes) ‖ ciphertext`. Support
`addKey`, `setPrimary`, `disable`, and `decrypt` routing by prefix. Write a test that simulates a full
rotation with mixed old and new ciphertexts.

## 9. Per-tenant rate limiter for the KMS (week 4)

Add a thread-safe token bucket per tenant in front of `InMemoryKms`. Test concurrent access with
an `ExecutorService` and an injectable clock. Discuss distributed rate limiting options
(local buckets with periodic sync vs central Redis).

## 10. Rotation scheduler (week 4)

Given key metadata (created, last rotated, policy: rotate every N days, destroy disabled versions after M
days), emit actions. Make it idempotent and testable with a fixed `Clock`. Discuss how it runs in
production (leader election, at-least-once, audit).

## 11. Blind index for searchable encryption (week 4)

Encrypt an `email` field with `EnvelopeEncryptor`, plus store `HMAC(indexKey, normalize(email))`
truncated to 16 bytes for lookups. Write down what leaks (equality, frequency) and how truncation
trades false positives for privacy.

## 12. Port to another language (any time)

Port `AesGcm`, `Hkdf`, and `EnvelopeEncryptor` to Go (`crypto/aes`, `crypto/cipher`,
`golang.org/x/crypto/hkdf`) or Rust (`aes-gcm`, `hkdf` crates). Generate test vectors from Java, and
check that the port decrypts Java's ciphertexts and vice versa (cross-language compatibility).

## 13. Cryptopals (stretch)

Do sets 1-3 of the Cryptopals challenges (https://cryptopals.com). Exercises such as ECB detection,
CBC padding oracle, and CTR nonce reuse make the "why" behind the rules in [01](01-crypto-fundamentals.md) memorable.

## 14. Mock interview loop (week 4)

- 45 min design: pick a prompt from [09](09-interview-question-bank.md), timebox, and draw diagrams.
- 45 min coding: pick a coding prompt, write tests first.
- 30 min behavioral: tell 3 stories from [08](08-staff-engineer-leadership.md) out loud, recorded.

Review the recordings and note where you rambled or skipped trade-offs.
