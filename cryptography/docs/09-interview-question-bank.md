# 09 Interview Question Bank

Answer out loud, then check yourself against the short answer. Docs referenced in brackets.

## Fundamentals [01]

1. **Encryption vs MAC vs signature vs hash?** Confidentiality; integrity+authenticity with a shared key;
   integrity+authenticity+non-repudiation with a public key; fingerprint with no key.
2. **Why AEAD?** Encryption alone is malleable; AEAD gives confidentiality and integrity in one
   primitive and binds context via AAD.
3. **What happens if you reuse a GCM nonce?** XOR of plaintexts leaks and the GHASH key can be
   recovered, enabling forgeries. Catastrophic.
4. **How many messages can one AES-GCM key encrypt with random 96-bit nonces?** About 2^32 to keep
   collision probability under 2^-32. Rotate or use XChaCha20 / AES-GCM-SIV / derived keys.
5. **Encrypt-then-MAC vs MAC-then-encrypt?** Encrypt-then-MAC; verify before decrypting to avoid
   padding oracles.
6. **Why not SHA-256 for passwords?** Too fast; use Argon2id/scrypt/bcrypt/PBKDF2 with salt and cost.
7. **HKDF extract vs expand?** Extract concentrates entropy into a PRK; expand derives labeled keys.
8. **What is forward secrecy?** Compromise of long-term keys doesn't reveal past sessions;
   achieved with ephemeral (EC)DH.
9. **RSA vs ECC?** ECC gives smaller keys and faster ops at equal security; RSA verify is fast;
   prefer Ed25519/ECDSA P-256 for new signatures and RSA-PSS/OAEP if RSA is required.
10. **What is a timing side channel and how do you avoid one?** Data-dependent timing leaks secrets;
    use constant-time comparisons and vetted implementations.
11. **What does post-quantum change?** Shor breaks RSA/ECC, so migrate key exchange first (ML-KEM
    hybrid) because of harvest-now-decrypt-later; Grover only halves symmetric security, so AES-256 is fine.

## Key management [02]

12. **Explain envelope encryption and why it exists.** DEK encrypts data locally; KEK in KMS/HSM wraps the
    DEK. Scales, and keeps root keys non-exportable, and rotation means rewrapping only.
13. **How do you rotate a KEK without downtime?** New version becomes primary for encrypt; old versions
    still decrypt; key id/version in ciphertext; background rewrap; disable then destroy old.
14. **What's the difference between disabling and destroying a key?** Disable is reversible; destroy
    is irreversible (crypto-shredding), so require a waiting period and multi-party approval.
15. **What is encryption context and why use it?** Non-secret AAD bound to ciphertext; prevents
    substitution, enables policy conditions, and appears in audit logs.
16. **How do you implement right-to-be-forgotten for backups?** Per-user/tenant keys; destroy the key.
17. **What's an HSM, and when do you need one?** Tamper-resistant device; keys non-exportable;
    FIPS 140-3 compliance; for root keys and CAs.
18. **What's the risk of caching data keys?** Larger blast radius if a host is compromised, and
    revocation is delayed. Bound it with max age/uses/bytes.
19. **How do you do BYOK/HYOK?** Customer key wraps your tenant KEK; customer can revoke; design for
    their availability and latency.

## Secrets management [03]

20. **Static vs dynamic secrets?** Dynamic secrets are minted per workload with a short TTL, so revoking and
    attributing them is easy.
21. **How does a pod authenticate to the secrets manager without a secret?** Platform-attested identity:
    projected service account token (OIDC) or SPIFFE SVID, verified by the secrets service.
22. **Secrets in env vars vs files?** Files (tmpfs) are preferred; env vars leak via crash dumps, `/proc`,
    child processes, and logging.
23. **How do you rotate a database password with zero downtime?** Dual credentials (A/B users),
    update consumers, then retire old.
24. **Leaked secret in git: what do you do?** Revoke/rotate first, then investigate usage, then
    purge history, then add prevention (pre-commit scanning).

## PKI and TLS [04]

25. **Walk through TLS 1.3 handshake.** ClientHello with key shares → ServerHello + key share → encrypted
    extensions, cert, CertificateVerify, Finished → client Finished. 1-RTT, PFS-only.
26. **How does mTLS work, and what identity goes in the cert?** Both sides present certs; use SPIFFE
    URI SAN for workloads.
27. **Revocation options and their trade-offs?** CRL (big, stale), OCSP (privacy, soft-fail), OCSP
    stapling, short-lived certificates (best for internal).
28. **Why is the root CA kept offline?** It is rarely needed, and if compromised you must replace trust
    stores everywhere.
29. **How would you rotate an intermediate CA?** Issue the new intermediate, distribute it in bundles,
    switch issuance, wait for old leaf expiry, retire the old intermediate.

## KMS system design [05]

30. **What are the KMS SLOs?** Availability ≥ 99.99% for decrypt; p99 latency in single-digit ms in-region.
31. **How do you make decrypt survive a regional outage?** Replicate key material (wrapped) to other
    regions; region-local decrypt; ciphertext carries key id.
32. **How do you prevent one tenant from starving others?** Per-tenant quotas, rate limiting,
    isolation of noisy neighbors, and client-side caching.
33. **How do you prove the audit log hasn't been tampered with?** Hash chain or Merkle tree with
    periodically signed roots, exported to WORM storage.
34. **How do you ensure no single admin can exfiltrate keys?** Non-exportable HSM keys, quorum
    (M-of-N) for sensitive operations, separation of duties, and alerting on policy changes.
35. **Consistency of key metadata?** Strong consistency (Raft/Paxos) for state changes like
    disable; caches with short TTL or push invalidation.

## Design prompts (45 min each)

- Design a KMS for a company with 5,000 microservices across 3 regions.
- Design a secrets manager with dynamic DB credentials and K8s integration.
- Design field-level encryption for a user profile store with search by email.
- Design a signing service for software artifacts (supply chain, Sigstore-like).
- Design an internal CA issuing hourly certs to 1M workloads.
- Design a tokenization vault for payment data.
- Plan a migration of all services from a legacy crypto library to a new one.
- Design key rotation for 10 PB of encrypted object storage.

## Coding prompts (implement in 30-45 min)

- Envelope encrypt/decrypt with a mock KMS (see `EnvelopeEncryptor`).
- An LRU + TTL cache with max-uses for data keys (`CachingDataKeySource`).
- Token bucket rate limiter per tenant, thread-safe.
- Parse a length-prefixed binary format safely (bounds checks!).
- Constant-time byte array equality.
- HKDF from HMAC (`Hkdf`).
- Shamir split/combine over GF(256) (`ShamirSecretSharing`).
- Hash-chained, append-only audit log with verification.
- Merkle tree with inclusion proofs.
- Key rotation scheduler: given keys with creation time and policy, emit rotation actions.
- Consistent hashing for routing tenants to KMS shards.

## Questions to ask the interviewer

- What are the biggest reliability or scale pain points for the platform today?
- How do product teams consume crypto today: library, sidecar, or service calls?
- What's the state of post-quantum readiness and crypto inventory?
- How is success measured for this team over the next year?
- What would you expect from someone at this level in the first 90 days?
