# Replication

**Use when:** data must survive node failure, reads exceed one node's capacity,
or copies must be closer to users.

## Replication models

- **Single leader:** one node orders writes; followers copy its log. It is
  operationally understandable and common for transactional data.
- **Multi-leader:** multiple leaders accept writes, usually across regions.
  Write availability improves, but conflicts need prevention or resolution.
- **Leaderless:** clients or coordinators write to and read from multiple
  replicas. Quorums, version reconciliation, and repair are part of the model.

With `N` replicas, a write quorum `W`, and read quorum `R`, the condition
`W + R > N` creates overlap, but it does not by itself guarantee linearizable
reads. Concurrent writes, sloppy quorums, clocks, and repair semantics matter.

## Synchronous versus asynchronous copies

Synchronous replication waits for another copy before acknowledging. It
improves durability and can provide stronger failover guarantees, but raises
write latency and may reduce availability.

Asynchronous replication acknowledges before followers catch up. It lowers
latency, but a leader failure can lose acknowledged writes and replicas can
serve stale data.

## Handling replica lag

Options include:

- Route a user's reads to the leader briefly after their write.
- Carry a commit position or version and wait until a replica has applied it.
- Pin one session to a replica for monotonic reads.
- Read critical entities from the leader and tolerate lag for aggregate views.
- Surface processing state instead of pretending an asynchronous update is
  immediately visible.

## Failure modes

- Automated failover can promote a stale follower and lose recent writes.
- Two nodes may both believe they are leader after a partition. Use consensus
  and fencing, not only a timeout.
- A returning node needs snapshot transfer or log catch-up without overwhelming
  the leader.
- Read scaling has limits because writes, indexes, and replication traffic still
  load the leader.
- Cross-region synchronous replication adds at least network round-trip latency.

## Interview cues

State the replication factor, acknowledgment rule, read routing, acceptable
lag, and failover mechanism. Do not say "add read replicas" without explaining
read-after-write behavior.
**Sound bite:** "Replication creates another copy; the acknowledgment and
promotion rules determine whether that copy improves durability, consistency,
or only read capacity."
