# Leader election, leases, and fencing

**Use when:** exactly one active coordinator should assign work, schedule a
singleton job, or control a resource at a time.

Use a consensus-backed coordination service or a database mechanism with clear
linearizable semantics. Candidates compete to create or renew a leadership
record. Leadership is temporary and must be safe to replace.

## Lease flow

1. A candidate acquires a lease with an expiry and a monotonically increasing
   fencing token.
2. It renews before expiry while performing work.
3. If renewal fails, it stops accepting new work.
4. A successor acquires a higher token.
5. The protected resource rejects commands carrying an older token.

The fencing token is critical. A paused former leader can wake after its lease
expired and still believe it is active. Time-based checks in that process do not
stop it from issuing stale writes; the resource must compare epochs or tokens.

## Trade-offs and failure modes

- Election causes a period with no leader; tune the failover target without
  making transient pauses trigger constant elections.
- A network partition can isolate the leader from the coordination quorum.
- Clock assumptions are dangerous. Prefer lease semantics enforced by one
  authoritative system and conservative timing.
- The leader can become a throughput bottleneck. Keep bulk data on partition
  owners and use the leader for metadata or assignment.
- A single global leader adds cross-region latency. Independent leaders per
  partition may scale better.
- Election callbacks and lock release are not sufficient without durable
  ownership checks on the target resource.

## When not to use it

Avoid a singleton when deterministic partitioning, idempotent duplicate work,
or a broker consumer group can provide ownership more simply. "Exactly one
worker" is often less robust than "duplicate work is safe."

## Interview cues

Name the coordination store, quorum behavior, lease duration, loss-of-leadership
response, fencing mechanism, and unavailable period during election.
**Sound bite:** "Election chooses a leader, but fencing is what prevents a stale
leader from corrupting the resource after ownership changes."
