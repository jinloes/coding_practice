# 06 Designing Developer-Friendly Crypto Libraries

A platform team's crypto library is used by hundreds of teams who are not cryptographers. Its job is
to make the secure path the easy path and the insecure path hard or impossible.

## Design principles

1. **Expose goals, not primitives.** Offer `Aead`, `DeterministicAead`, `StreamingAead`, `Mac`,
   `PublicKeySign`/`Verify`, `HybridEncrypt` (the model Google Tink popularized), not "AES with a mode,
   padding, and IV".
2. **No knobs that can be set wrong.** The library picks the nonce (see `AesGcm`), the mode, tag size,
   and parameters. Choices that remain are named templates: `AES256_GCM`, `XCHACHA20_POLY1305`.
3. **Keys are opaque objects, not byte arrays.** Callers can't log, serialize, or compare them
   accidentally. Exporting raw material requires an explicitly named "insecure" access token or API.
4. **Fail closed with one error type.** Decrypt failure says "decryption failed", never why (no
   padding/MAC oracles). Signature verify returns false on any error.
5. **Self-describing ciphertext.** Prefix every output with format version and key id. This is what
   enables rotation and algorithm migration.
6. **Integrate with the KMS by default.** Keysets are stored encrypted by a KMS KEK; the library
   fetches/unwraps them using workload identity. Nobody pastes keys into config.
7. **Make the right thing observable.** Metrics on operations per key id, failures, cache hit rate,
   and deprecated algorithm use, so the platform team can drive migrations with data.
8. **Language parity.** Same formats and behavior across Java, Go, Python, Rust, etc., verified by
   cross-language test vectors so a Go service can decrypt what a Java service wrote.

## Keysets and rotation inside the library

A keyset is an ordered set of keys with one **primary**:

```
keyset {
  primaryKeyId: 0x2a7c...
  keys: [
    {id: 0x11f0..., template: AES256_GCM, status: ENABLED,  outputPrefix: TINK},
    {id: 0x2a7c..., template: AES256_GCM, status: ENABLED,  outputPrefix: TINK},   ← encrypts
    {id: 0x9b03..., template: AES128_GCM, status: DISABLED, outputPrefix: LEGACY},
  ]
}
```

- Ciphertext = 5-byte prefix (version byte + 4-byte key id) ‖ primitive output.
- Decrypt reads the prefix and selects the key directly; for raw/legacy outputs it tries candidate keys.
- Rotation: add new key (non-primary) → roll out keyset everywhere → promote to primary → re-encrypt
  or let data age out → disable → delete. Same two-phase shape as KMS rotation.

## Crypto agility and fleet-wide migrations

Example: "migrate 800 services from AES-CBC+HMAC (homegrown) to the library's AES-GCM" or "add hybrid
post-quantum KEM".

1. **Inventory**: static analysis (Semgrep/CodeQL rules for `Cipher.getInstance`, `crypto/aes`,
   hard-coded keys), dependency graphs, and runtime telemetry from the library.
2. **Read-both, write-old** → **read-both, write-new** → **read-new only**. Ship decrypt support for the
   new format everywhere before anyone writes it (readers before writers, like schema evolution).
3. **Backfill** stored data with idempotent, checkpointed, rate-limited jobs; verify by sampling decrypts.
4. **Guardrails**: lint rules and build checks that block new uses of deprecated APIs; dashboards per
   team; deadlines with escalation paths agreed with leadership.
5. **Make migration cheap**: codemods, wrapper APIs with identical signatures, office hours, migration
   guides. The platform team absorbs complexity so product teams don't have to.

## Field-level / application-layer encryption

Use when data must be protected from the database operators, analytics pipelines, backups, or other
services, not just from disk theft.

- Encrypt specific columns (PII) in the app with envelope encryption; AAD = table + primary key + column.
- **Querying encrypted data**: equality lookups via a **blind index** (HMAC of normalized value with a
  separate key) or deterministic AEAD; range queries need order-revealing/preserving schemes (leaky,
  avoid) or redesign. Be explicit about what each option leaks.
- **Tokenization**: replace sensitive values with random tokens; mapping stored in a hardened vault.
  Reduces PCI scope; format-preserving encryption (FF1) when downstream systems need the original format.
- Key granularity: per tenant or per user to enable crypto-shredding.

## Streaming and large objects

Single-shot AEAD needs the whole message in memory and only verifies at the end. For large files use
a **streaming AEAD**: split into segments, each with its own nonce derived from (header nonce,
segment index, last-segment flag) so segments can't be reordered, dropped, or truncated without detection.

## Performance considerations

- AES-GCM with AES-NI does multiple GB/s per core; crypto is rarely the bottleneck. Remote calls,
  allocation, and serialization are.
- Reuse `Cipher`/`Mac` instances per thread where the platform allows (thread-local), avoid
  re-parsing keysets per call, and cache KMS unwraps.
- JVM specifics: providers (SunJCE, BouncyCastle, Conscrypt, Amazon Corretto Crypto Provider) differ
  in speed by several times; benchmark with JMH, not ad-hoc loops.

## Testing a crypto library

- **Known-answer tests** from standards (NIST CAVP vectors, RFC appendices; see `HkdfTest`).
- **Project Wycheproof** test vectors (edge cases: invalid curves, malformed signatures, bad tags).
- Cross-language compatibility vectors checked into the repo.
- Negative tests for every failure path: tampered ciphertext, wrong AAD, wrong key, truncated input,
  oversized length fields (see the length-validation fix in `EnvelopeEncryptor.parse`).
- Fuzzing parsers (Jazzer, go-fuzz, cargo-fuzz).
- Property tests: `decrypt(encrypt(x)) == x` for random inputs; serialization round trips.
- Constant-time checks for comparison code (dudect-style statistical tests) where it matters.

## Review checklist for code that uses crypto

- [ ] Uses the platform library or a vetted high-level API, not raw primitives.
- [ ] No hard-coded keys, IVs, salts used as keys, or keys in config/logs.
- [ ] AEAD with context bound as AAD; no ECB; no unauthenticated CBC/CTR.
- [ ] Nonces generated by the library; never reused.
- [ ] Randomness from a CSPRNG.
- [ ] Constant-time comparisons for MACs/tokens.
- [ ] Ciphertext format is versioned and includes key id.
- [ ] Errors don't distinguish failure causes to callers.
- [ ] Keys come from KMS via workload identity; rotation path exists and is tested.
- [ ] Plaintext and keys are not logged, cached unboundedly, or written to disk.
