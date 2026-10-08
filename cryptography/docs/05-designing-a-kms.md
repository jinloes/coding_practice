# 05 System Design: Build a Key Management Service

Practice this as a 45-60 minute interview. Do it on paper first, then compare with this walkthrough.
The structure (requirements → API → data model → architecture → scale → failures → security →
operations) works for any infra design prompt.

## 1. Clarify requirements (5 minutes)

Ask, don't assume. Typical answers:

**Functional**
- Create/describe keys; rotate; enable/disable; schedule deletion.
- Encrypt / Decrypt small payloads (≤ 4 KB); GenerateDataKey; ReEncrypt.
- Sign / Verify, and asymmetric keys (maybe phase 2).
- Per-key IAM policies; encryption context; audit of every call.

**Non-functional**
- Availability: 99.99%+ for Decrypt (it's on every read path in the company). Encrypt can be slightly lower.
- Latency: p99 < 10 ms in-region for Decrypt.
- Scale: say 500k-1M requests/sec across regions, 10M+ keys, 10k client services.
- Durability: losing key material = losing data. Needs 11+ nines and backups that are themselves protected.
- Security: key material never leaves the trust boundary in plaintext; compromise of one host doesn't
  expose all keys; insider resistance (no single operator can extract keys).
- Compliance: FIPS 140-3 validated crypto, data residency per region.

State priorities explicitly: **durability and confidentiality of keys > availability of decrypt >
availability of encrypt/admin > latency > cost**.

## 2. API (5 minutes)

```
CreateKey(spec: AES_256 | EC_P256 | ..., usage, policy, tags, multiRegion) -> keyId
RotateKey(keyId) -> version              // or automatic schedule
DisableKeyVersion / EnableKeyVersion / ScheduleKeyDeletion(keyId, days)
Encrypt(keyId, plaintext, context) -> blob
Decrypt(blob, context) -> plaintext       // keyId and version come from the blob
GenerateDataKey(keyId, context) -> {plaintext, wrapped}
ReEncrypt(blob, oldContext, newKeyId, newContext) -> blob   // plaintext never leaves service
Sign(keyId, digest) / Verify(...) / GetPublicKey(keyId)
```

Points to call out: idempotency keys on CreateKey; the blob is self-describing (format version, key
id, version, algorithm, nonce, ciphertext, tag); all admin ops are audited and policy-gated.

## 3. Data model

```
Key:        keyId, ownerAccount, spec, usage, state, primaryVersion, policyRef, region(s), createdAt
KeyVersion: keyId, version, state, wrappedMaterial (by domain key), domainKeyVersion, createdAt
Policy:     principals × actions × conditions (context keys, source identity)
AuditEvent: time, principal, action, keyId, version, context, result, requestId
```

Key material is stored **wrapped by a regional domain key that lives only in HSMs**. The metadata
store therefore never holds usable key material and can be an ordinary replicated database.

## 4. Architecture

```
         clients (with local SDK: envelope encryption + unwrap cache)
                           │ mTLS + workload identity
                     ┌─────▼──────┐
                     │  Frontend  │  authn, request validation, rate limiting, routing
                     └─────┬──────┘
               ┌───────────┼───────────────┐
               ▼           ▼               ▼
         Policy engine   Key metadata    Audit pipeline ──► append-only log store / SIEM
         (authz, cached) store (replicated,  (async, but durable before ack for
                         cached in hosts)    sensitive ops: fail closed)
                           │
                    ┌──────▼───────┐
                    │ Crypto hosts │  unwrap KEK with domain key, do AEAD, zero memory
                    └──────┬───────┘
                           │ domain key ops only
                     ┌─────▼──────┐
                     │  HSM fleet │  holds domain keys; quorum-controlled
                     └────────────┘
```

Two flavors of the crypto tier; know both:
- **All ops in HSM**: KEKs unwrapped and used inside the HSM. Strongest boundary, limited by HSM
  throughput (thousands-tens of thousands ops/sec/device), expensive to scale.
- **Hardened crypto hosts**: HSM unwraps KEK (or derives it) and hands it to a hardened, minimal host
  (no SSH, attested boot, memory locked, key cached briefly). Much higher throughput. This is how large
  cloud KMSs reach very high QPS while keeping domain keys in HSMs.

## 5. Scale and performance (back-of-envelope)

- 1M Decrypt/sec, AES-GCM on 32-byte payloads: CPU cost is microseconds; the cost is TLS, authz,
  metadata lookup, and audit logging. ~20-50k req/s per host → 20-50 hosts per region plus headroom.
- Audit volume: 1M events/s × ~500 bytes = 500 MB/s ≈ 43 TB/day. Needs a streaming pipeline (Kafka-like),
  compression, partitioning by key/account, tiered storage, and sampling **only** for non-sensitive debug
  logs (never for the security audit log).
- Metadata: 10M keys × few versions × ~1 KB = tens of GB; fits in memory caches on every frontend.
  Writes (create/rotate) are rare, so read-mostly replication with change streams works well.
- Reduce load at the source: the client SDK does envelope encryption, caches unwrapped DEKs (short
  TTL, bounded size), and caches data keys for encryption (bounded uses/age). This often removes
  90%+ of calls. Note the trade-off: revocation latency = cache TTL.

## 6. Availability and multi-region

- Regional cells: each region is independent for Decrypt (keys replicated or regional). No cross-region
  call on the hot path.
- Within a region: multiple AZs, stateless frontends behind load balancers, HSM clusters with N+2.
- **Cell-based architecture / shuffle sharding**: partition customers across cells so a poison request
  or noisy neighbor can't take out everyone.
- Multi-region keys: replicate (wrapped) key material to selected regions so failover readers can
  decrypt. Replication is async; rotation must reach all replicas before promotion (two-phase).
- Degradation: if the policy service is down, use cached policy with a bounded staleness; if audit
  logging is down, sensitive ops **fail closed** (decide and document which ones).
- Load shedding and per-tenant rate limits protect the service from a single client's retry storm;
  client SDKs use exponential backoff with jitter and circuit breakers.
- Deployment safety: staged rollouts by cell and region, automated rollback, and **crypto
  compatibility tests**: every new build must decrypt a corpus of ciphertexts produced by every
  previous format version.

## 7. Security design

- Key material never in plaintext at rest; in memory only on hardened hosts, zeroed after use.
- Insider threat: no human can read key material; prod access requires multi-party approval and is
  logged; HSM admin needs quorum.
- Authenticated, authorized every call: mTLS + workload identity, then policy (key policy ∧ caller IAM ∧ context conditions).
- Ciphertext binds key id/version/context as AAD, so blobs can't be replayed against another key/tenant.
- Defense against confused deputies: service A can't use B's key just because it can reach the API.
- Monitoring: anomalous decrypt rates, new principals, repeated failures (probing), admin ops on
  high-value keys page security.
- Supply chain: reproducible builds, signed artifacts, attested hosts.

## 8. Durability and disaster recovery

- Wrapped key material replicated across AZs and to backup storage; domain keys backed up in HSMs
  in separate facilities (HSM-to-HSM wrapped export, quorum-controlled).
- Regularly **test restores** (a backup you haven't restored is a hope, not a backup).
- Key deletion has a mandatory waiting period, and anything decrypt-attempted during it alarms.
- Region loss: multi-region keys allow continued decrypt; single-region keys mean data in that region
  is unavailable (and that's by design for residency).

## 9. Operations

- SLOs: Decrypt availability and p99 latency per region and per cell; error budget policy.
- Dashboards: QPS by API/tenant, cache hit rates on SDK, HSM utilization, audit lag.
- Runbooks: HSM failure, domain-key rotation, mass key-disable during an incident, tenant offboarding.
- Capacity planning: HSM ops headroom, audit pipeline throughput, quotas per tenant.

## Follow-up questions interviewers ask

- How do you rotate the domain key (the key that wraps all KEKs)? *(New domain key version in HSMs →
  re-wrap all KEK versions in the background with checkpoints → verify → retire old domain key. KEKs
  themselves don't change, so no customer impact.)*
- How do you support 10x QPS next year? *(SDK caching, more cells, hardened crypto host tier, HSM pooling.)*
- A client accidentally deleted a key. *(Pending-deletion window, cancel deletion, alerting during the window.)*
- What if a crypto host is compromised? *(Exposure limited to KEKs it unwrapped recently; short cache
  lifetime; attestation to detect; rotate affected KEKs; DEKs re-wrapped.)*
- How would you add post-quantum support? *(New key specs, hybrid KEM for asymmetric wrapping, versioned
  blob format, SDK capability negotiation, fleet migration plan.)*

## Related prompts to practice with the same skeleton

- Secrets manager with dynamic credentials (see [03](03-secrets-management.md)).
- Field-level encryption for a large social graph / user database with search on encrypted fields.
- Internal certificate authority issuing 1M short-lived certs per hour.
- Tokenization service for payment card or national ID numbers (format-preserving vs vault tokenization).
- Tamper-evident audit log (hash chains / Merkle trees, signed checkpoints).
- Distributed rate limiter or config service (the non-crypto distributed systems round).
