# Key Management & Cryptography Study Guide

A four-week plan for a **Staff-level backend engineer on a key management / cryptography platform
team**: the people who run the KMS, the secrets manager, and the crypto libraries every other
engineer uses.

It has two parts:

1. **Notes** in [`docs/`](docs): concepts, design walkthroughs, trade-offs, and a question bank.
2. **Runnable reference code** in `src/`: small, tested implementations of the core building blocks
   using only the JDK. Read them, break them, then rebuild them from scratch as drills.

```bash
./gradlew :cryptography:test
```

## What a Staff interview for this kind of role usually probes

| Area | What "strong" looks like |
|---|---|
| Crypto fundamentals | Picks the right primitive and says *why*; knows the failure modes (nonce reuse, unauthenticated encryption, timing leaks). Never proposes rolling their own cipher. |
| Key management | Key hierarchies, envelope encryption, rotation without downtime, lifecycle states, crypto-shredding, HSM trust boundaries. |
| Secrets management | Secret zero, workload identity, dynamic short-lived credentials, leases and revocation, Kubernetes integration. |
| Distributed systems design | Designs a multi-region KMS / secrets service for high QPS and 99.99%+ availability; reasons about caching, consistency, failure modes, and blast radius. |
| Developer-facing libraries | Misuse-resistant API design, crypto agility, versioned ciphertext formats, safe migrations across thousands of services. |
| Cloud & Kubernetes | Cloud KMS/IAM models, KMS-backed etcd encryption, service identity, mTLS / PKI. |
| Coding | Clean, tested code under time pressure; often a data-structure problem, sometimes a crypto-flavored one (serialization format, key rotation, caching, rate limiting). |
| Staff behaviors | Leading multi-team programs, driving consensus, raising the bar for others, making and documenting trade-offs. |

## Four-week plan

Aim for ~2 hours on weekdays and a longer block on weekends. Each week ends with a self-check: if
you can't explain an item out loud in two minutes without notes, revisit it.

### Week 1: Cryptography fundamentals

| Day | Read | Do |
|---|---|---|
| 1 | [01 Fundamentals](docs/01-crypto-fundamentals.md): threat models, symmetric crypto, block-cipher modes | Run `CommonPitfallsTest`; explain each failure |
| 2 | 01: AEAD, AES-GCM vs ChaCha20-Poly1305, nonces | Read `AesGcm`; re-implement it from memory |
| 3 | 01: hashes, MACs, KDFs, password hashing | Read `Hkdf`, `PasswordHasher`; implement HKDF from RFC 5869 without looking |
| 4 | 01: asymmetric crypto, signatures, key exchange | Read `Signatures`, `X25519Handshake` |
| 5 | 01: randomness, side channels, post-quantum | Write a one-page "which primitive when" cheat sheet |
| Weekend | [04 PKI & TLS](docs/04-pki-and-tls.md) | Walk through a TLS 1.3 handshake on a whiteboard; [exercises](docs/10-exercises.md) 1-2 |

**Self-check:** Why is AES-GCM nonce reuse catastrophic? Why HKDF for DH output but not for passwords?
Why does encrypt-then-MAC beat MAC-then-encrypt? What does forward secrecy require?

### Week 2: Key management and secrets

| Day | Read | Do |
|---|---|---|
| 1 | [02 Key management](docs/02-key-management.md): hierarchies, envelope encryption | Read `EnvelopeEncryptor`; draw the key hierarchy |
| 2 | 02: lifecycle, rotation, re-encryption, crypto-shredding | Read `InMemoryKms`; trace rotation and `rewrap` in the tests |
| 3 | 02: HSMs, root of trust, multi-party control | Read `ShamirSecretSharing`; explain unseal ceremonies |
| 4 | [03 Secrets management](docs/03-secrets-management.md): secret zero, workload identity | Diagram how a pod gets a DB password with no static credential |
| 5 | 03: dynamic secrets, leases, rotation, leak response | Write the runbook for "a private key was committed to git" |
| Weekend | [07 Cloud & Kubernetes](docs/07-cloud-and-kubernetes.md) | Exercises 3-5 |

**Self-check:** Rotate a KEK without re-encrypting a petabyte. Delete one tenant's data from
immutable backups. Explain why the encryption context belongs in the audit log.

### Week 3: System design at Staff scope

| Day | Read | Do |
|---|---|---|
| 1 | [05 Designing a KMS](docs/05-designing-a-kms.md): requirements, API, data model | Do the design in 45 minutes on paper, *then* read the walkthrough |
| 2 | 05: scale, caching, multi-region, failure modes | Do back-of-envelope numbers for 1M decrypts/sec |
| 3 | [06 Crypto library design](docs/06-crypto-library-design.md) | Design the API for an encryption library used by 1,000 services |
| 4 | 06: crypto agility, migrations, rollout | Plan a fleet-wide migration from one algorithm to another |
| 5 | Practice prompts in [09 Question bank](docs/09-interview-question-bank.md) | Two timed mock designs (secrets manager; field-level encryption) |
| Weekend | Review weak spots | Exercises 6-8 |

### Week 4: Leadership, coding, and mocks

| Day | Read | Do |
|---|---|---|
| 1 | [08 Staff engineer leadership](docs/08-staff-engineer-leadership.md) | Draft 6 stories in the STAR(R) format |
| 2 | 08: influence, consensus, AI-assisted development | Rehearse stories out loud; time them to ~3 minutes |
| 3 | Coding drills | Two medium problems + rebuild `InMemoryKms` from scratch in 45 minutes |
| 4 | Question bank rapid-fire | Answer 30 questions out loud |
| 5 | Full mock: 45 min design + 45 min coding + 30 min behavioral | Ask a peer to play interviewer |
| Weekend | Rest and light review | Re-read your cheat sheet and stories |

## Reference code map

| Package | Class | Concepts |
|---|---|---|
| `aead` | `AesGcm` | AEAD, random nonces, associated data, versionless wire format |
| `kdf` | `Hkdf` | RFC 5869 extract/expand, domain separation via `info` |
| `kms` | `InMemoryKms`, `EncryptionContext` | Key rings, rotation, lifecycle states, self-describing ciphertext, authenticated headers, audit log |
| `envelope` | `EnvelopeEncryptor`, `CachingDataKeySource` | DEK/KEK, rewrap after rotation, data-key caching trade-offs |
| `signature` | `Signatures` | Ed25519 vs ECDSA P-256, fail-closed verification |
| `keyexchange` | `X25519Handshake` | ECDH, transcript binding, forward secrecy |
| `password` | `PasswordHasher` | Salted slow hashing, constant-time compare, parameter upgrades |
| `secretsharing` | `ShamirSecretSharing` | k-of-n splitting over GF(2^8), unseal ceremonies |
| test `pitfalls` | `CommonPitfallsTest` | ECB pattern leakage, CTR/GCM nonce reuse, malleability |

The code is for learning. In production, use a vetted, high-level library (for example Google
Tink, libsodium, or AWS Encryption SDK) and a managed KMS/HSM.

## Recommended external resources

- *Serious Cryptography* (2nd ed.), Jean-Philippe Aumasson: best single book for practitioners.
- *Real-World Cryptography*, David Wong: protocols, TLS, secure messaging, PQC.
- Dan Boneh's *Cryptography I* (Coursera), and Boneh & Shoup, *A Graduate Course in Applied Cryptography* (free PDF).
- Cryptopals challenges, sets 1-3: break the things in `CommonPitfallsTest` yourself.
- NIST SP 800-57 Part 1 (key management), SP 800-38D (GCM), SP 800-108 / 800-56C (KDFs).
- RFC 5869 (HKDF), RFC 8446 (TLS 1.3), RFC 9106 (Argon2), RFC 8439 (ChaCha20-Poly1305).
- AWS KMS Cryptographic Details whitepaper: the clearest public description of a production KMS.
- Google Tink design docs and "Key management" docs; HashiCorp Vault architecture and seal docs.
- *Designing Data-Intensive Applications*, Martin Kleppmann: for the distributed systems half.
- *Staff Engineer*, Will Larson, and *The Staff Engineer's Path*, Tanya Reilly: for the leadership half.
