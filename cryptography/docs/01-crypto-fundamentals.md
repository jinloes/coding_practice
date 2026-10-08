# 01 Cryptography Fundamentals

Goal: pick the right primitive, explain the security property it gives, and name how it fails.

## Threat modeling first

Before choosing any algorithm, answer:

- **What are we protecting?** Confidentiality, integrity, authenticity, availability, non-repudiation.
- **From whom?** Network attacker, malicious insider, compromised host, cloud provider, backup thief.
- **Where is the data?** In transit, at rest, in use (memory, logs, crash dumps, caches).
- **What does the attacker get if one key leaks?** That is the blast radius and it drives key hierarchy design.

Kerckhoffs's principle: a system must stay secure when everything except the key is public. All the
security lives in the key, which is why key management is the hard part.

## Security goals vocabulary

| Term | Meaning |
|---|---|
| IND-CPA | Ciphertexts reveal nothing even if the attacker can get chosen plaintexts encrypted. Requires randomized (or nonce-based) encryption. |
| IND-CCA2 | Still secure when the attacker can get chosen ciphertexts decrypted (except the target). Requires integrity; AEAD gives this. |
| EUF-CMA | Unforgeability for MACs/signatures: can't produce a valid tag for any new message even after seeing many. |
| Forward secrecy | Compromise of long-term keys doesn't reveal past session traffic. Requires ephemeral key exchange. |
| Post-compromise security | Sessions heal after a key compromise (ratcheting; Signal's double ratchet). |
| Semantic security | Informal synonym for IND-CPA. |

## Symmetric encryption

### Block ciphers

AES is a 128-bit block cipher with 128/192/256-bit keys. A block cipher alone only encrypts 16 bytes;
a **mode of operation** turns it into an encryption scheme.

| Mode | Notes | Use? |
|---|---|---|
| ECB | Each block encrypted independently. Identical blocks give identical ciphertext (the "ECB penguin"). See `CommonPitfallsTest.ecbLeaksPatterns`. | Never |
| CBC | Chained with a random IV. Needs padding, so it is the source of padding-oracle attacks (POODLE, Lucky 13) when not authenticated. | Legacy only, and only with encrypt-then-MAC |
| CTR | Turns AES into a stream cipher: keystream = AES(key, nonce‖counter). Parallelizable, no padding. Malleable and catastrophically broken on nonce reuse. | Only inside an AEAD |
| GCM | CTR + GHASH polynomial MAC = AEAD. Hardware accelerated (AES-NI + PCLMULQDQ). | Default |
| XTS | Tweakable mode for disk sectors; length preserving, no integrity. | Full-disk encryption only |

### AEAD: authenticated encryption with associated data

AEAD gives confidentiality **and** integrity for the plaintext, plus integrity for **associated data**
(AAD) that is not encrypted (headers, record ids, tenant ids, key ids). Decryption either returns the
exact original plaintext or fails; it never returns partial or modified data.

API shape: `Encrypt(key, nonce, plaintext, aad) -> ciphertext‖tag`, `Decrypt(key, nonce, ct‖tag, aad)
-> plaintext | error`.

Use AAD to **bind context**: if you encrypt a user's SSN with AAD `user_id=42`, an attacker who can
write to the database can't swap that ciphertext into user 43's row, because decryption with
`user_id=43` fails. `InMemoryKms` uses this to bind its header and encryption context.

| AEAD | Key / nonce | Notes |
|---|---|---|
| AES-GCM | 128/256-bit key, 96-bit nonce | Fast with hardware AES. Random 96-bit nonces: NIST limits to 2^32 messages per key (collision risk). Nonce reuse leaks plaintext XOR **and the GHASH authentication key**, enabling forgeries. |
| ChaCha20-Poly1305 | 256-bit key, 96-bit nonce | Fast in software, constant-time without hardware support. Preferred on mobile and older CPUs. |
| XChaCha20-Poly1305 | 192-bit nonce | Random nonces are safe for practically unlimited messages. Great choice when you can't track counters. |
| AES-GCM-SIV | 96-bit nonce | **Nonce-misuse resistant**: reusing a nonce only reveals whether two plaintexts were identical. Slightly slower (two passes). |
| AES-SIV | Deterministic AEAD | Same plaintext + AAD always gives the same ciphertext. Used for key wrapping and for searchable/joinable encrypted columns. Leaks equality, by design. |

Nonce strategies:
- **Random**: simplest; respect the per-key message limit and rotate keys.
- **Counter**: unique as long as state is never lost or duplicated, which is hard across restarts,
  replicas, and VM snapshots.
- **Derive per-message keys**: e.g., HKDF(key, random salt) per message, which raises GCM's limits.

### Encrypt-then-MAC

When composing a cipher and a MAC manually: encrypt first, then MAC the ciphertext (and IV), and
verify the MAC **before** decrypting. MAC-then-encrypt (old TLS) and encrypt-and-MAC (old SSH) both
led to real attacks. Better: just use an AEAD.

### Malleability

Stream ciphers and CTR mode are malleable: flipping a ciphertext bit flips the same plaintext bit.
`CommonPitfallsTest.unauthenticatedEncryptionIsMalleable` turns "$100" into "$900" without the key.
Encryption without authentication is not secure encryption.

## Hash functions

Properties: preimage resistance, second-preimage resistance, collision resistance (birthday bound:
~2^(n/2) work for an n-bit hash).

| Hash | Status |
|---|---|
| MD5, SHA-1 | Broken for collisions (SHAttered, 2017). Never for signatures or integrity. |
| SHA-256 / SHA-512 (SHA-2) | Standard. Vulnerable to **length extension**, so `SHA256(secret‖msg)` is not a MAC. |
| SHA-3 / SHAKE | Sponge construction; no length extension. |
| BLAKE2 / BLAKE3 | Very fast, keyed modes built in. |

## Message authentication codes

A MAC proves a message came from someone holding the shared key and was not modified.

- **HMAC**: `H((K ⊕ opad) ‖ H((K ⊕ ipad) ‖ m))`. Safe with SHA-2 despite length extension.
- **KMAC** (SHA-3), **Poly1305** (one-time key; used inside AEADs), **GMAC**.
- **Always compare tags in constant time** (`MessageDigest.isEqual`, `hmac.compare_digest`,
  `crypto/subtle.ConstantTimeCompare`). Early-exit comparison leaks the tag byte by byte through timing.

Common uses: webhook signatures, signed cookies/tokens, API request signing (AWS SigV4), CSRF tokens.

## Key derivation functions

| KDF | Input | Purpose |
|---|---|---|
| HKDF (RFC 5869) | High-entropy secret (DH output, master key) | Extract uniform key, then expand to many keys. `info` gives **domain separation** (e.g., `"enc"` vs `"mac"`, or per-tenant keys from one master). See `Hkdf`. |
| NIST SP 800-108 (KDF in counter mode) | Key | Common inside HSMs and KMSs for deriving per-object keys. |
| PBKDF2 | Password | Slow via iteration count. Not memory hard. FIPS approved. |
| scrypt, Argon2id | Password | Memory hard, resists GPU/ASIC cracking. Argon2id is the current recommendation (RFC 9106). |
| bcrypt | Password | Still acceptable; 72-byte input limit. |

Password storage: unique random salt per password, slow memory-hard hash, store parameters with the
hash so they can be upgraded on next login (`PasswordHasher.needsRehash`), optional **pepper** (a
secret key held in a KMS/HSM, applied via HMAC) so a stolen database alone is useless.

## Randomness

- Use the OS CSPRNG: `/dev/urandom`, `getrandom(2)`, `SecureRandom`, `crypto/rand`, `secrets` (Python).
- Never use `java.util.Random`, `math/rand`, or `random` for keys, nonces, tokens, or IDs that must
  be unguessable.
- Failure stories: Debian OpenSSL (2008) reduced keys to 32,767 possibilities; Android `SecureRandom`
  bug (2013) led to ECDSA nonce reuse and stolen Bitcoin; VM snapshot/clone duplicating RNG state.

## Asymmetric cryptography

### RSA

Security from integer factorization. Use 2048-bit minimum (3072 for long-lived keys).
- Encryption: **RSA-OAEP** only. PKCS#1 v1.5 encryption is vulnerable to Bleichenbacher padding oracles (ROBOT, 2017).
- Signatures: **RSA-PSS** preferred; PKCS#1 v1.5 signatures are still widely used and acceptable.
- Slow, big keys and signatures. Mostly seen in legacy PKI and key wrapping (e.g., BYOK import).

### Elliptic curves

Same security with far smaller keys: a 256-bit curve ≈ 128-bit security ≈ RSA-3072.
- **P-256 (secp256r1)**: NIST curve, universal HSM and FIPS support.
- **Curve25519**: X25519 for key exchange, **Ed25519** for signatures. Designed to be hard to
  implement wrong (no invalid-curve pitfalls, constant time, deterministic signing).

### Digital signatures

Provide authenticity, integrity, and **non-repudiation** (only the private key holder could sign;
anyone can verify). Uses: code signing, JWTs, certificates, software supply chain (Sigstore), audit
log integrity.

- **ECDSA** needs a fresh, secret, unbiased nonce per signature. Reuse or bias leaks the private key
  (PlayStation 3, 2010; numerous wallet thefts). RFC 6979 makes it deterministic.
- **Ed25519** is deterministic by design. See `SignaturesTest.ed25519IsDeterministicEcdsaIsNot`.
- Verification APIs must **fail closed**: `Signatures.verify` returns false on any exception.

### Key exchange

Diffie-Hellman lets two parties agree on a shared secret over a public channel.
- Use **ephemeral** keys (ECDHE) for forward secrecy: compromise of the server's long-term key later
  doesn't decrypt recorded traffic.
- DH alone is unauthenticated, so it's vulnerable to a man in the middle. TLS authenticates it with a
  signature from the certificate key over the handshake transcript.
- Never use the raw shared secret as a key. Run it through HKDF with the transcript, as
  `X25519Handshake` does.

### Hybrid encryption

Public-key encryption of bulk data = generate a random symmetric key, encrypt the data with an AEAD,
encrypt the symmetric key to the recipient's public key. Standardized as **HPKE** (RFC 9180), used by
MLS and Encrypted Client Hello. This is the asymmetric cousin of envelope encryption.

## Side channels and implementation attacks

- **Timing**: non-constant-time comparisons, table lookups indexed by secrets (old software AES), branch on secret bits.
- **Padding / error oracles**: different errors or timing for "bad padding" vs "bad MAC". Return one generic error.
- **Fault injection, power analysis**: why HSMs and smart cards exist.
- **Memory exposure**: keys in heap dumps, logs, core files, swap. Zero buffers when done (best effort
  in GC languages), keep `toString()` from printing keys, never log plaintext or keys.
- **Speculative execution** (Spectre/Meltdown): another argument for isolating key material in HSMs, enclaves, or separate processes.

## Post-quantum cryptography

A large quantum computer running Shor's algorithm breaks RSA, DH, and ECC. Grover's algorithm only
halves symmetric security, so AES-256 and SHA-384+ remain fine.

- NIST standards (Aug 2024): **ML-KEM** (FIPS 203, Kyber) for key encapsulation, **ML-DSA** (FIPS 204,
  Dilithium) and **SLH-DSA** (FIPS 205, SPHINCS+) for signatures. FN-DSA (Falcon) is forthcoming.
- **Harvest now, decrypt later**: an attacker records traffic today to decrypt in the future, so
  key exchange is migrating first. Major browsers and TLS stacks already ship hybrid
  **X25519MLKEM768** (classical + PQ, secure if either holds).
- Signatures can migrate later (forgery requires a quantum computer at signing time), but long-lived
  roots of trust (firmware, CA roots) need planning now.
- The real lesson for a platform team is **crypto agility**: inventory where every algorithm is used,
  make algorithms configurable and ciphertext self-describing, and be able to roll a change fleet-wide.

## Cheat sheet: which primitive when

| Need | Use |
|---|---|
| Encrypt data with a shared key | AES-256-GCM or ChaCha20-Poly1305 (XChaCha20 for random nonces at scale) |
| Encrypt huge files or streams | Streaming AEAD (segmented, each chunk authenticated, e.g., Tink StreamingAead) |
| Deterministic encryption for lookup/joins | AES-SIV / AES-GCM-SIV deterministic mode, accepting equality leakage |
| Encrypt to someone's public key | HPKE |
| Integrity with a shared key | HMAC-SHA256 |
| Prove who signed something | Ed25519 (or ECDSA P-256 when HSM/FIPS requires it) |
| Agree on a key over a network | X25519 ECDHE (+ ML-KEM hybrid) |
| Derive keys from a key | HKDF |
| Store passwords | Argon2id (or bcrypt, scrypt, PBKDF2 for FIPS) |
| Random tokens and IDs | 128+ bits from the OS CSPRNG |
| Wrap keys with keys | AES-KW (RFC 3394), AES-KWP (RFC 5649), or AES-GCM/AES-SIV |
