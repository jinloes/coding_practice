# 04 PKI and TLS

## X.509 certificates

A certificate binds a public key to an identity (DNS name, SPIFFE ID, email) and is signed by an
issuer. Key fields: subject, **Subject Alternative Name** (what clients actually check), issuer,
validity (`notBefore`/`notAfter`), public key, key usage / extended key usage (serverAuth,
clientAuth, codeSigning), basic constraints (`CA:true`, path length), serial number.

Chain of trust: **leaf ← intermediate CA ← root CA**. Clients trust a small set of roots; servers
send leaf + intermediates. Roots stay offline (often in an HSM in a vault); intermediates do the
day-to-day issuing so they can be revoked and replaced without touching every client.

Validation steps a client performs: build a chain to a trusted root, check signatures, validity
dates, name match against SAN, key usage, name constraints, and revocation status.

## Revocation

| Mechanism | Notes |
|---|---|
| CRL | Signed list of revoked serials; large; clients often skip fetching |
| OCSP | Online check per cert; privacy and availability issues; often soft-fail |
| OCSP stapling | Server attaches a fresh OCSP response; better, still uneven support |
| Short-lived certs | Validity of hours/days makes revocation mostly unnecessary. **The modern answer for internal PKI.** |

Public web certificate lifetimes are shrinking (CA/Browser Forum has approved a phased reduction
toward 47 days by 2029), so automated issuance (ACME) is mandatory.

## Internal PKI for service-to-service mTLS

- Each workload gets a short-lived cert (e.g., 1-24h) with its service identity in the SAN (SPIFFE
  ID URI or DNS name).
- Issuance is automated: workload attests (K8s SA token, cloud identity) to an issuer (cert-manager,
  SPIRE, Vault PKI, cloud private CA, service mesh CA like Istio's istiod).
- Private keys are generated **on the workload** and never leave it; only a CSR is sent.
- Renew at ~50-70% of lifetime with jitter; alert on certs nearing expiry (expired certs remain one of
  the most common causes of major outages).
- Trust bundle rotation is two-phase: distribute new CA to all trust stores, then start issuing
  from it, then remove the old CA after all leaves have expired.
- Authorization is separate from authentication: mTLS tells you *who*; a policy decides *what they may do*.

## TLS 1.3 handshake (1-RTT)

```
Client                                              Server
ClientHello: versions, cipher suites,
  key_share (X25519 / X25519MLKEM768 public key),
  SNI, ALPN                        ───────►
                                                    ServerHello: chosen suite, key_share
                                         [handshake keys derived via HKDF from ECDHE secret]
                                   ◄───────         {EncryptedExtensions}
                                                    {Certificate}
                                                    {CertificateVerify: sig over transcript hash}
                                                    {Finished: HMAC over transcript}
{Finished}                         ───────►
[application data, keys derived from the full transcript]
```

What to know:
- Only AEAD suites (AES-GCM, ChaCha20-Poly1305) and ephemeral (EC)DHE, so **forward secrecy is mandatory**.
- Everything after ServerHello is encrypted, including the certificate.
- `CertificateVerify` is the signature that authenticates the DH exchange (prevents MITM).
- The key schedule is a chain of HKDF-Extract/Expand calls over the transcript hash.
- 0-RTT resumption (early data) is **replayable**; only allow it for idempotent requests.
- Removed from 1.2: RSA key transport, CBC suites, compression, renegotiation, custom DH groups.

TLS 1.2 attacks worth recognizing by name: BEAST, CRIME, POODLE, Lucky 13, Heartbleed (memory bug, not
protocol), FREAK/Logjam (export-grade downgrades), ROBOT (RSA PKCS#1 v1.5 oracle).

## Token formats (JWT / JWS / JWE)

- Signed JWTs (JWS) prove integrity and issuer; they are **not encrypted**. Don't put secrets in them.
- Validate `alg` against an allow list (the `alg: none` and RS256→HS256 confusion attacks), `iss`,
  `aud`, `exp`, `nbf`. Fetch keys by `kid` from JWKS and cache with rotation in mind.
- Prefer short expiry plus refresh tokens; revocation of stateless tokens is otherwise hard.
- PASETO and Biscuit are alternatives designed to avoid JOSE's algorithm agility foot-guns.

## Code and artifact signing

Supply-chain integrity: sign build artifacts and container images, verify at deploy (admission
controller). Sigstore (cosign + Fulcio + Rekor) issues short-lived signing certs from OIDC identity
and records signatures in a transparency log, removing long-lived signing keys. SLSA levels describe
build provenance maturity.
