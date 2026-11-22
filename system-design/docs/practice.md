# Practice drills

Reading creates recognition; timed design creates recall. Use the
[interview playbook](interview-playbook.md) and the
[design template](template.md) for each drill.

The [case study prompt deck](case-studies/practice-prompts.md) provides complete
mock-interview question sets for all eight worked cases.

## A repeatable study loop

1. Pick a prompt and spend two minutes listing ambiguous requirements.
2. Run a 10-minute version: scope, one estimate, API, diagram, bottleneck.
3. Add one failure scenario and explain recovery.
4. Compare each added component to a simpler alternative.
5. Repeat the prompt with one changed constraint.

Useful constraint changes:

- Traffic grows 10x.
- Writes now outnumber reads.
- The service must survive a regional outage.
- Read-after-write consistency becomes mandatory.
- One tenant produces half of all traffic.
- A downstream service is unavailable for an hour.
- Data must remain in a user's home region.

## Prompt ladder

| Level | Prompt | Patterns to exercise |
| --- | --- | --- |
| 1 | [URL shortener](case-studies/url-shortener.md) | Key generation, caching, replication |
| 1 | [API rate limiter](case-studies/api-rate-limiter.md) | Token bucket, partitioning, fail-open policy |
| 1 | Paste service | Object storage, metadata, expiration |
| 2 | [Notification service](case-studies/notification-platform.md) | Pub/sub, preference filtering, retries, idempotency |
| 2 | Web crawler | Frontier queues, deduplication, politeness limits |
| 2 | Search autocomplete | Prefix index, caching, offline aggregation |
| 3 | [Home news feed](case-studies/news-feed.md) | Fan-out, partitioning, materialized views |
| 3 | [Chat service](case-studies/chat-system.md) | Persistent connections, ordering, offline delivery |
| 3 | [Video platform](case-studies/video-platform.md) | Upload workflow, transcoding queues, CDN |
| 4 | [Payment workflow](case-studies/payment-processing.md) | Idempotency, ledger invariants, saga |
| 4 | [Ride-hailing dispatch](case-studies/ride-hailing.md) | Geospatial indexing, realtime state, leases |
| 4 | Collaborative editor | Operation ordering, conflict resolution, snapshots |
| 4 | Multi-region ticket sale | Contention, reservations, strong consistency |

The [case study track](case-studies/README.md) contains worked answers for the
linked prompts. Attempt each design before reading its solution.

## Rapid-fire pattern questions

Answer each in under two minutes:

1. Why can adding a cache make a database outage more likely?
2. How do you provide read-your-writes when reads normally use replicas?
3. Why does at-least-once delivery require an atomic deduplication boundary?
4. What makes a good partition key, and how would you handle a hot tenant?
5. When is fan-out on read better than fan-out on write?
6. Why can retries turn a partial outage into a total outage?
7. What does a fencing token prevent that a lease alone does not?
8. When would direct CDC be worse than a transactional outbox?
9. Why is a compensation not equivalent to rollback?
10. What must be true before calling an operation "exactly once"?

## Self-review rubric

Score each dimension from 0 to 2:

| Dimension | 0 | 1 | 2 |
| --- | --- | --- | --- |
| Scope | Solved an undefined product | Some assumptions stated | Scope and exclusions explicit |
| Estimates | None | Numbers without consequences | Numbers justify design choices |
| API and data | Generic boxes only | Partial contracts | Access patterns and keys are clear |
| Data flow | Paths are unclear | Happy path only | Reads, writes, and async boundaries clear |
| Pattern selection | Technology dumping | Reasonable pattern | Pressure, alternative, and trade-off explained |
| Consistency | Unspecified | "Eventual" or "strong" globally | Guarantees tied to operations and invariants |
| Failure handling | None | Clean crash considered | Slowdown, retry, overload, and recovery covered |
| Communication | Unstructured | Mostly understandable | Prioritized, signposted, and responsive |

A score below 12 suggests the fundamentals need work. A high score does not
require the most components; simpler and well-defended usually scores better.
