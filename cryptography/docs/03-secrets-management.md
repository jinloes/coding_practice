# 03 Secrets Management

Secrets: database passwords, API tokens, signing keys, TLS private keys, OAuth client secrets,
encryption keys handed to apps. A secrets platform stores them, delivers them to the right workload,
rotates them, and audits every access.

## Anti-patterns to eliminate

- Secrets in source code, config repos, container images, CI logs, or environment variables visible in
  process listings and crash reports.
- Long-lived static credentials shared by many services or humans.
- Manual rotation ("we'll rotate it when someone leaves").
- Secrets readable by every pod in a namespace or every engineer on a team.

## The "secret zero" problem

To fetch secrets, a workload must authenticate. If authentication uses a secret, where does *that*
come from? Solve it with **identity the platform already vouches for**, not a bootstrapped password:

| Environment | Identity source |
|---|---|
| Kubernetes | Projected **service account tokens** (audience-bound, short-lived, auto-rotated OIDC JWTs) verified via TokenReview or the cluster's OIDC discovery/JWKS |
| AWS | IAM roles for EC2 instances / ECS tasks, IRSA or EKS Pod Identity |
| Azure | Managed identities; AKS Workload Identity (federated credentials) |
| GCP | Service accounts via metadata server; GKE Workload Identity Federation |
| Anywhere | **SPIFFE/SPIRE**: node and workload attestation issue short-lived X.509 or JWT SVIDs with IDs like `spiffe://example.org/ns/payments/sa/api` |
| Bare metal | TPM-based attestation, provisioning-time enrollment certificates |

Principle: the workload proves *what it is* (attested by the scheduler, cloud, or hardware), then
exchanges that for short-lived credentials. No static secret ever ships with the workload.

## Architecture of a secrets manager (Vault-like)

```
 clients ──auth──► API layer ──policy check──► secret engines ──► storage backend (encrypted)
                       │                            │
                   audit devices              dynamic creds (DB, cloud IAM, PKI)
                       │
                 barrier key ◄── unsealed by KMS auto-unseal or Shamir key shares
```

- **Encryption barrier**: everything in storage is encrypted with a keyring; the keyring is encrypted
  with a root key; the root key is protected by the **seal** (cloud KMS / HSM auto-unseal, or Shamir
  shares held by operators). Storage compromise alone yields nothing.
- **Auth methods** map external identities (K8s SA, cloud IAM, OIDC, mTLS cert) to policies.
- **Policies**: path-based, deny by default, least privilege.
- **Secret engines**: static KV (versioned), dynamic credentials, PKI (issue certs), transit
  (encryption as a service: the app sends data, never sees the key).
- **Audit devices**: request and response logged with HMAC'd sensitive values; if audit fails,
  requests fail (fail closed).
- **HA**: active/standby with a consensus-backed store (Raft); performance standbys/replicas serve reads.

## Static vs dynamic secrets

| | Static | Dynamic |
|---|---|---|
| Example | Third-party API key | DB user created on demand |
| Lifetime | Until rotated | Minutes to hours (lease) |
| Sharing | Often shared by all replicas | Unique per workload instance |
| Revocation | Rotate and redeploy everywhere | Revoke one lease; blast radius = one pod |
| Attribution | "Someone with the key" | Exact workload in the DB audit log |

**Leases**: every dynamic secret has a TTL; clients renew; the server revokes at expiry or on demand.
The server must track leases durably and revoke reliably (e.g., `DROP USER`) even after restarts;
revocation queues with retries and idempotency.

## Rotation without downtime

The general pattern is **dual credentials** (two valid at once):

1. Create credential B while A is still valid.
2. Distribute B to all consumers (push or wait for pull/refresh interval).
3. Verify consumers use B (metrics/audit).
4. Revoke A.

Examples: DB users with two alternating accounts, API keys with "primary/secondary", JWT signing key
sets (JWKS with `kid`), TLS cert renewal at ~2/3 of lifetime.

Consumers must **re-read secrets without restart**: file watchers on mounted secrets, SDK refresh with
jittered polling, connection pools that rebuild on auth failure.

## Delivering secrets to Kubernetes workloads

| Approach | How | Trade-offs |
|---|---|---|
| Native K8s `Secret` | Stored in etcd, mounted as files/env | Base64 is not encryption. Enable **etcd encryption at rest with a KMS provider (v2)**, tight RBAC (`get/list secrets` is effectively root for a namespace). |
| External Secrets Operator | Syncs from external manager into K8s Secrets | Simple for apps; secret still lands in etcd |
| Secrets Store CSI Driver | Mounts secrets from external manager as an in-memory volume at pod start | Avoids etcd (optional sync); provider-specific; rotation via remount polling |
| Sidecar / init agent | Agent authenticates with SA token, writes to shared tmpfs, renews leases | Rich features (templating, dynamic creds); extra container per pod |
| Direct SDK | App calls secrets API using workload identity | Most control, best for dynamic secrets; every app needs the library (your team's library!) |

Prefer files on tmpfs over environment variables: env vars leak via `/proc`, child processes, crash
dumps, and debug endpoints.

## Leak detection and response

Prevention: pre-commit and CI secret scanning (gitleaks, trufflehog, GitHub push protection), log
scrubbing, typed `Secret` wrappers whose `toString()` is redacted.

Runbook when a secret leaks:
1. **Contain**: revoke/rotate immediately; for keys, disable the key version.
2. **Scope**: audit logs for every use since the leak window; who/what used it.
3. **Eradicate**: remove from history if needed, but assume it's already copied.
4. **Recover**: verify consumers on new credential; re-enable traffic.
5. **Learn**: blameless postmortem; fix the class of bug (e.g., add scanning, move to dynamic creds).

Design so that step 1 is one API call, takes seconds, and doesn't cause an outage. That is the
measurable goal of a secrets platform.

## Encryption as a service (transit)

Apps call `encrypt(keyName, plaintext, context)` on the secrets/KMS service and store the ciphertext;
they never hold keys. Pros: central rotation and audit, no key handling in apps. Cons: network hop
per operation, throughput limits, service on the critical path. Hybrid: the library does envelope
encryption locally and only calls the service for DEK wrap/unwrap.

## Interview prompts to practice

- Design a secrets manager for 10k services across 3 regions and 2 clouds.
- How does a new pod get its DB password, and how do you rotate it weekly without restarts?
- An engineer pasted a production API key in a public channel. Walk me through the next hour.
- Kubernetes Secrets are "just base64". What would you change?
- How do you handle secret-manager outages? (Cache last-known-good with expiry, fail static not fail
  open, regional replicas, avoid making every request call the manager.)
