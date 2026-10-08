# 07 Cloud and Kubernetes Security

## Cloud KMS comparison

| | AWS KMS | Azure Key Vault / Managed HSM | GCP Cloud KMS |
|---|---|---|---|
| Key container | Key (alias), regional | Vault or Managed HSM pool | Key ring → key → versions, location-scoped |
| Access control | Key policy + IAM + grants | Azure RBAC (or legacy access policies) | IAM on key ring/key |
| Encryption context | Yes (`EncryptionContext`) | No native equivalent (use AAD in your format) | `additionalAuthenticatedData` |
| HSM-backed | All keys in FIPS-validated HSMs; CloudHSM for single tenant | Premium tier / Managed HSM (single tenant) | `HSM` protection level; Cloud HSM |
| External keys | XKS (External Key Store) | Managed HSM, BYOK | Cloud EKM |
| Multi-region | Multi-Region keys (replicated material) | Per-region vaults; backup/restore within geography | Multi-region / global locations |
| Envelope helper | `GenerateDataKey`, AWS Encryption SDK | SDK wrap/unwrap | Tink integration |
| Limits | Per-account request quotas (thousands to tens of thousands RPS by region/op) | Per-vault throttling | Per-project quotas |

Interview angle: because every cloud throttles KMS, **client-side caching and envelope encryption
are not optional at scale**. Know that cross-account/tenant access is a policy design problem
(confused deputy; AWS uses condition keys like `aws:SourceAccount` and `kms:ViaService`).

## Cloud IAM essentials

- Principals: users, roles, service accounts / managed identities, federated workload identities.
- Prefer **short-lived, federated credentials** (OIDC federation from K8s/CI) over long-lived access keys.
- Least privilege, separate admin and use permissions, permission boundaries / org policies (SCPs) to
  set guardrails no account can exceed.
- Resource policies vs identity policies; evaluation logic (explicit deny wins).

## Kubernetes security model (what a KMS/secrets team touches)

### Secrets at rest

- K8s Secrets are base64 in etcd by default. Enable **encryption at rest** via `EncryptionConfiguration`.
- **KMS provider v2** (GA in 1.29): API server generates a DEK, encrypts Secrets locally, and has the
  external KMS plugin wrap the DEK with a remote KEK. A DEK is reused across writes until the KEK
  changes or the API server restarts, so the external KMS isn't called per secret write. KEK rotation is
  detected through the plugin's key id. This is envelope encryption + data-key caching (the same pattern as `CachingDataKeySource`).
- Rotating to a new key: update config, restart API servers, then rewrite all secrets
  (`kubectl get secrets -A -o json | kubectl replace -f -`) so they're encrypted with the new key.
- Protect etcd backups; they contain everything.

### Identity and access

- **Service accounts** with projected, audience-bound, time-limited tokens (`TokenRequest` API).
  Disable automounting where not needed.
- RBAC: `get`/`list`/`watch` on Secrets in a namespace grants every secret there. Also `create pods`
  in a namespace effectively grants its secrets (mount them). Audit for these.
- **Workload identity federation**: cloud IAM trusts the cluster's OIDC issuer, mapping a K8s service
  account to a cloud role. No cloud keys in the cluster.

### Network and runtime

- NetworkPolicies (default deny), service mesh mTLS (Istio, Linkerd) with SPIFFE identities.
- Pod Security Standards (restricted), read-only root filesystems, no privileged containers, seccomp.
- Admission control (OPA Gatekeeper / Kyverno): require signed images, forbid secrets in env vars,
  enforce labels.
- Node compromise = all secrets mounted on that node, so prefer short-lived dynamic credentials.

### Running a KMS/secrets service on Kubernetes

- Stateless frontends as Deployments with PodDisruptionBudgets, topology spread across zones, HPA on
  QPS/CPU, priority classes so they're the last thing evicted.
- Stateful parts (Raft storage) as StatefulSets with anti-affinity, or kept outside the cluster.
- **Bootstrapping circularity**: the cluster's own secrets/etcd encryption may depend on the KMS you're
  running in that cluster. Run the root KMS/secrets tier on separate infrastructure (or a separate
  cluster) with its own break-glass path.
- HSM access from pods: network HSMs reached via a dedicated proxy/sidecar with mTLS; client
  certificates for the HSM partition held only by that tier.

## Confidential computing (good to know)

Trusted execution environments (Intel SGX/TDX, AMD SEV-SNP, AWS Nitro Enclaves) encrypt memory and
provide **remote attestation**. A KMS can release a key only to an enclave whose measured code hash it
trusts (AWS KMS supports Nitro Enclave attestation conditions). Useful for "data in use" protection
and for reducing trust in cloud/infra operators.
