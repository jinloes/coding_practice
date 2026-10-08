# 02 Key Management

Encryption turns a large data-protection problem into a small key-protection problem. Key management
is how you solve the small problem at scale.

## Key hierarchy

```
            Root key / HSM master key     (never leaves HSM; protected by quorum of operators)
                       │ wraps
          Key encryption keys (KEKs)      (per service / tenant / data class; live in KMS)
                       │ wrap
          Data encryption keys (DEKs)     (per object / file / record batch; stored wrapped beside data)
                       │ encrypt
                     Data
```

Why layers:
- **Performance**: bulk encryption happens locally with DEKs; KMS only sees 32-byte keys.
- **Blast radius**: a leaked DEK exposes one object; a leaked KEK exposes one tenant/service, not everything.
- **Rotation**: rotating a KEK means re-wrapping small DEKs, not re-encrypting petabytes.
- **Access control**: IAM policy on a KEK controls who can unwrap DEKs, which controls data access.
- **Crypto-shredding**: destroy the KEK (or DEK) and the data is gone, even in immutable backups.

## Envelope encryption

Encrypt (see `EnvelopeEncryptor`):
1. Call `GenerateDataKey(kekId, context)`; get `{plaintextDek, wrappedDek}`.
2. Encrypt data locally with the DEK using an AEAD.
3. Store `wrappedDek ‖ ciphertext` together; zero the plaintext DEK.

Decrypt:
1. Parse the envelope; send `wrappedDek` + context to KMS `Decrypt`.
2. KMS checks IAM policy and context, logs the request, unwraps.
3. Decrypt data locally.

Key insight: **the wrapped DEK is self-describing** (it names its KEK and version), so readers don't
need to know which key was used and rotation doesn't break old data.

Design choices to discuss:
- **DEK granularity**: per object (best isolation; more KMS calls), per file/partition, per tenant per
  hour (cached). Tie this to write rate and blast radius requirements.
- **Data-key caching** (`CachingDataKeySource`): reuse a DEK for N messages or T seconds. Cuts KMS
  load, latency, and cost; increases blast radius; makes the audit log coarser. Bound by count, age,
  and bytes. Never share across encryption contexts.
- **Unwrap caching** on the read path: cache `wrappedDek -> plaintextDek` in memory with a short TTL.
  Huge win for hot data; revocation (key disable) then takes up to one TTL to take effect. That delay
  is a product decision to make explicit.

## Encryption context / AAD

Non-secret key-value pairs bound to the ciphertext: `{tenant: acme, table: invoices, purpose: pii}`.
- Must match exactly on decrypt (prevents ciphertext swapping/confused deputy).
- Appears in audit logs, so you can answer "who decrypted tenant acme's PII last Tuesday?".
- Can be referenced in IAM policies (e.g., allow decrypt only when `tenant == caller's tenant`).
- Must be **canonicalized** (sorted, length prefixed) before use as AAD. See `EncryptionContext`.

## Key lifecycle (NIST SP 800-57)

```
 pre-activation ─► active ─► deactivated (decrypt only) ─► compromised? ─► destroyed
                     ▲  │
                     └──┘ suspended / disabled (reversible)
```

| State | Encrypt | Decrypt | Notes |
|---|---|---|---|
| Enabled + primary | ✔ | ✔ | Exactly one primary version per key |
| Enabled, not primary | ✘ | ✔ | Older versions after rotation |
| Disabled | ✘ | ✘ | Reversible kill switch for incidents |
| Pending deletion | ✘ | ✘ | Mandatory waiting period (cloud KMSs use 7-30 days) to catch mistakes |
| Destroyed | ✘ | ✘ | Irreversible; data under it is gone |

Every transition is an audited, access-controlled API. Destroy should require a waiting period, alarms
on any use attempt during it, and ideally multi-party approval.

**Cryptoperiod**: how long a key may be used. Driven by algorithm limits (e.g., 2^32 random-nonce
GCM messages), compliance (often annual rotation), and risk.

## Rotation

Rotation adds a new version; it does not by itself re-encrypt existing data.

1. **Automatic KEK rotation**: KMS adds a version and makes it primary. New wraps use it; old
   ciphertexts still decrypt with the old version (the blob names its version). Zero downtime, zero
   client changes. See `InMemoryKmsTest.oldCiphertextStillDecryptsAfterRotation`.
2. **Rewrap / re-encrypt DEKs** under the new version (`EnvelopeEncryptor.rewrap`) so old versions can
   eventually be disabled and destroyed. Cheap: touches only wrapped keys. A KMS `ReEncrypt` API does
   this server-side so the DEK never leaves the HSM boundary.
3. **Full data re-encryption**: required only if a DEK itself is suspected compromised. Expensive;
   run as a background backfill with checkpoints, rate limits, and verification.

Rotation in a distributed system:
- New version must be **visible to all decrypters before any encrypter uses it** (otherwise readers in
  another region fail). Pattern: add version as "decrypt-ready" everywhere → wait for propagation
  (or confirm via version watermark) → promote to primary. Same two-phase idea as rotating a TLS cert
  or a JWT signing key (publish new public key in JWKS first, then start signing).
- Track usage per version (metrics from audit logs) so you know when an old version is truly unused
  before disabling it. Disable first, wait, then schedule destruction.

## Crypto-shredding and data deletion

Deleting data from every replica, cache, log, and immutable backup is hard. Instead, encrypt each
deletion unit (user, tenant) under its own key and destroy the key. Requirements:
- Key per deletion unit (per-user DEKs or per-user KEKs), so destroy affects only that unit.
- Plaintext must not linger elsewhere (search indexes, analytics copies, logs).
- Destruction must be verifiable and audited (GDPR "right to erasure" evidence).
- Watch for caches holding the plaintext key until TTL expiry.

## Hardware security modules (HSMs)

Tamper-resistant devices that generate and use keys without ever exporting them in plaintext.
- **FIPS 140-2/140-3 Level 3** is the usual bar: tamper evidence and response, identity-based auth.
- Interfaces: PKCS#11, KMIP, JCE providers, vendor SDKs.
- Throughput: thousands to tens of thousands of symmetric ops/sec per device. Far too slow and costly
  for every data operation, which is another reason for the key hierarchy.
- **Pattern used by large KMSs**: HSMs hold only root/domain keys. Customer KEKs are stored outside
  the HSM *wrapped by a domain key*, and are unwrapped inside the HSM (or a hardened host) only for the
  duration of an operation. This makes KMS storage horizontally scalable while the root of trust stays in hardware.
- Cloud options: AWS CloudHSM, Azure Managed HSM / Dedicated HSM, GCP Cloud HSM; managed KMSs are
  themselves backed by HSMs.

## Root of trust and multi-party control

- **Key ceremonies**: generating or restoring root keys is a scripted, recorded event with multiple
  officers, tamper-evident bags, and audit witnesses.
- **Quorum / M-of-N**: no single person can export, restore, or unseal. Implemented with smart cards,
  HSM quorum auth, or **Shamir's secret sharing** (`ShamirSecretSharing`): split the unseal key into
  5 shares, any 3 reconstruct it, 2 reveal nothing.
- **Separation of duties**: the people who administer keys aren't the people who can use them to
  decrypt data. Key administrators can't read data; data owners can't delete keys without approval.

## Access control and audit

- Every key has a policy: who can **administer** (create, rotate, disable, change policy) vs who can
  **use** (encrypt, decrypt, generate data key). Keep these separate.
- Prefer granting service identities, not humans. Humans get break-glass access with approval and alerting.
- **Grants / scoped delegation**: temporary, narrowly scoped permission for another service to use a key.
- **Audit everything**: caller identity, key id/version, operation, encryption context, result,
  source IP/workload, time. Never log plaintext. Ship logs to tamper-evident, append-only storage.
- Detection: alert on decrypt spikes, new callers, failed decrypts (probing), use of
  disabled/pending-deletion keys.

## Key ownership models

| Model | Who holds the root | Trade-off |
|---|---|---|
| Provider-managed | Cloud/platform owns the keys | Easiest; protects against disk theft, not against provider |
| Customer-managed (CMK) | Customer controls policy/rotation in provider KMS | Revocable access, audit visibility |
| BYOK | Customer generates key on-prem, imports wrapped (RSA-OAEP / AES-KWP) | Provenance and escrow; provider still holds it |
| HYOK / external key store | Key stays in customer's HSM; provider calls out for each unwrap | Maximal control; customer is now in the availability path |

## Multi-tenant and regional concerns

- **Data residency**: keys and their HSMs may have to stay in a region; ciphertext replicated
  elsewhere is unreadable without a cross-region call (or a regional replica key).
- **Multi-region keys**: same key material replicated to several regions so data encrypted in one can
  be decrypted in another during failover. Trade residency/isolation for availability.
- **Tenant isolation**: per-tenant KEKs make tenant offboarding (shred) and per-tenant audit simple and
  limit blast radius; cost is more keys and more cache misses.

## Things to say in an interview

- "Self-describing ciphertext (format version, key id, key version, algorithm) is what makes rotation
  and algorithm migration possible without coordination."
- "I'd separate key administration from key usage, and both from data access."
- "KMS is on the critical path of every read, so availability of KMS is availability of the product.
  I'd design caching and degradation behavior deliberately and get product sign-off on revocation latency."
- "Rotation is a two-phase rollout: distribute for decrypt, then promote for encrypt, then verify
  old-version usage hits zero before disabling."
