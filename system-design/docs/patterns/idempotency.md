# Idempotency and deduplication

**Use when:** clients, networks, workers, or brokers may repeat an operation.

An operation is idempotent when applying the same logical request more than once
has the same externally visible effect as applying it once.

## API idempotency keys

For a create or payment-like operation:

1. The client generates a unique idempotency key for one logical request.
2. The server stores the key, a hash of the request, status, and result.
3. The domain write and idempotency record commit atomically.
4. A retry with the same key and payload returns the stored result.
5. Reusing the key with a different payload is rejected.

Keep records longer than the maximum retry window. Scope keys by caller or
tenant so unrelated clients cannot collide or retrieve each other's result.
Represent in-progress operations so two concurrent requests do not both run.

## Idempotent event consumers

Common techniques:

- Store each event ID in an inbox table in the same transaction as the state
  change.
- Make the event carry an entity version and reject older or repeated versions.
- Use an upsert to set a desired state instead of incrementing blindly.
- Use a naturally unique business key and a database uniqueness constraint.

A check followed by a separate write is racy. The deduplication marker and
effect need one atomic boundary, or a conditional write that combines them.

## Limits

- Deduplication state has retention and storage cost.
- An idempotent database update does not make an external email or charge
  idempotent. The downstream system also needs a stable key.
- Exactly-once claims must name the boundary. A broker transaction cannot make
  arbitrary third-party side effects atomic.
- Some operations need compensation rather than idempotency.

## Interview cues

Always discuss idempotency when introducing retries or at-least-once delivery.
Name the key, storage location, atomic boundary, retention window, and response
to a key reused with different input.
**Sound bite:** "We accept duplicate delivery and make the business effect
effectively once by committing a durable deduplication key with the state
change."
